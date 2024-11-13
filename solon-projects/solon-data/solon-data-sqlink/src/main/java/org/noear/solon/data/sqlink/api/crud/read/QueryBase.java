/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.data.sqlink.api.crud.read;

import io.github.kiryu1223.expressionTree.delegate.Action1;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.LambdaExpression;
import org.noear.solon.data.sqlink.annotation.RelationType;
import org.noear.solon.data.sqlink.api.crud.CRUD;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.IMappingTable;
import org.noear.solon.data.sqlink.base.metaData.NavigateData;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeFactory;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeSet;
import org.noear.solon.data.sqlink.base.toBean.build.ObjectBuilder;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;
import org.noear.solon.data.sqlink.core.page.PagedResult;
import org.noear.solon.data.sqlink.core.page.Pager;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.visitor.GroupByVisitor;
import org.noear.solon.data.sqlink.core.visitor.HavingVisitor;
import org.noear.solon.data.sqlink.core.visitor.NormalVisitor;
import org.noear.solon.data.sqlink.core.visitor.SelectVisitor;
import org.noear.solon.data.sqlink.core.visitor.methods.AggregateMethods;
import org.noear.solon.data.sqlink.core.visitor.methods.LogicExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.isBool;

/**
 * @author kiryu1223
 * @since 3.0
 */
public abstract class QueryBase extends CRUD {
    public final static Logger log = LoggerFactory.getLogger(QueryBase.class);

    private final QuerySqlBuilder sqlBuilder;

    public QueryBase(QuerySqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    protected QuerySqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    protected SqLinkConfig getConfig() {
        return sqlBuilder.getConfig();
    }

    protected boolean any() {
        SqLinkConfig config = getConfig();
        //获取拷贝的查询对象
        ISqlQueryableExpression queryableCopy = getSqlBuilder().getQueryable().copy(config);
        QuerySqlBuilder querySqlBuilder = new QuerySqlBuilder(config, queryableCopy);
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        // SELECT 1
        querySqlBuilder.setSelect(factory.select(Collections.singletonList(factory.constString("1")), int.class));
        // LIMIT 1
        querySqlBuilder.setLimit(0, 1);
        //查询
        SqlSession session = getConfig().getSqlSessionFactory().getSession(config);
        List<SqlValue> values = new ArrayList<>();
        String sql = querySqlBuilder.getSqlAndValue(values);
        tryPrintSql(log, sql);
        return session.executeQuery(ResultSet::next, sql, values);
    }

    protected <T> List<T> toList() {
        SqLinkConfig config = getConfig();
        boolean single = sqlBuilder.isSingle();
        List<FieldMetaData> mappingData = single ? Collections.emptyList() : sqlBuilder.getMappingData();
        List<SqlValue> values = new ArrayList<>();

        //long start = System.nanoTime();
        String sql = sqlBuilder.getSqlAndValue(values);
        //System.out.println("本次toSql耗时" + (System.nanoTime() - start));

        //tryPrintUseDs(log, config.getDataSourceManager().getDsKey());
        tryPrintSql(log, sql);
        Class<T> targetClass = (Class<T>) sqlBuilder.getTargetClass();
        SqlSession session = config.getSqlSessionFactory().getSession(config);

        //long start2 = System.nanoTime();
        List<T> ts = session.executeQuery(
                r -> ObjectBuilder.start(r, targetClass, mappingData, single, config).createList(),
                sql,
                values
        );
        //System.out.println("本次toBean耗时" + (System.nanoTime() - start2));

        if (!sqlBuilder.getIncludeSets().isEmpty()) {
            try {
                IncludeFactory includeFactory = config.getIncludeFactory();
                includeFactory.getBuilder(getConfig(), session, targetClass, ts, sqlBuilder.getIncludeSets(), sqlBuilder.getQueryable()).include();
            }
            catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return ts;
    }

    protected <T> T first() {
        ISqlQueryableExpression queryableCopy = getSqlBuilder().getQueryable().copy(getConfig());
        QuerySqlBuilder querySqlBuilder = new QuerySqlBuilder(getConfig(), queryableCopy);
        querySqlBuilder.getIncludeSets().addAll(getSqlBuilder().getIncludeSets());
        LQuery<T> lQuery = new LQuery<>(querySqlBuilder);
        lQuery.limit(1);
        List<? extends T> list = lQuery.toList();
        return list.isEmpty() ? null : list.get(0);
    }

    protected void distinct0(boolean condition) {
        sqlBuilder.setDistinct(condition);
    }

    protected <R> EndQuery<R> select(Class<R> r) {
        select0(r);
        return new EndQuery<>(boxedQuerySqlBuilder());
    }

    protected boolean select(LambdaExpression<?> lambda) {
        SelectVisitor selectVisitor = new SelectVisitor(getConfig(), sqlBuilder.getQueryable());
        ISqlExpression expression = selectVisitor.visit(lambda);
        ISqlSelectExpression selectExpression;
        if (expression instanceof ISqlSelectExpression) {
            selectExpression = (ISqlSelectExpression) expression;
        }
        else {
            SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
            // 用于包装某些数据库不支持直接返回bool
            if (isBool(lambda.getReturnType())) {
                SqLinkConfig config = getConfig();
                switch (config.getDbType()) {
                    case SQLServer:
                    case Oracle:
                        expression = LogicExpression.IfExpression(config, expression, factory.constString("1"), factory.constString("0"));
                }
            }
            selectExpression = factory.select(Collections.singletonList(expression), lambda.getReturnType(), true, false);
        }
        sqlBuilder.setSelect(selectExpression);
        return sqlBuilder.isSingle();
    }

    protected void select0(Class<?> c) {
        sqlBuilder.setSelect(c);
    }

    protected void join(JoinType joinType, Class<?> target, ExprTree<?> expr) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression on = normalVisitor.visit(expr.getTree());
        sqlBuilder.addJoin(joinType, factory.table(target), on);
    }

    protected void join(JoinType joinType, QueryBase target, ExprTree<?> expr) {
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression on = normalVisitor.visit(expr.getTree());
        sqlBuilder.addJoin(joinType, target.getSqlBuilder().getQueryable(), on);
    }

    protected void where(LambdaExpression<?> lambda) {
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression where = normalVisitor.visit(lambda);
        sqlBuilder.addWhere(where);
    }

    protected void orWhere(LambdaExpression<?> lambda) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression where = normalVisitor.visit(lambda);
        sqlBuilder.addOrWhere(where);
    }

    protected void exists(Class<?> table, LambdaExpression<?> lambda, boolean not) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression where = normalVisitor.visit(lambda);
        int offset = sqlBuilder.getQueryable().getOrderedCount();
        QuerySqlBuilder querySqlBuilder = new QuerySqlBuilder(getConfig(), factory.queryable(factory.from(factory.table(table), offset)));
        querySqlBuilder.setSelect(factory.select(Collections.singletonList(factory.constString("1")), table, true, false));
        querySqlBuilder.addWhere(where);
        ISqlUnaryExpression exists = factory.unary(SqlOperator.EXISTS, querySqlBuilder.getQueryable());
        if (not) {
            sqlBuilder.addWhere(factory.unary(SqlOperator.NOT, exists));
        }
        else {
            sqlBuilder.addWhere(exists);
        }
    }

    protected void exists(QueryBase queryBase, LambdaExpression<?> lambda, boolean not) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression where = normalVisitor.visit(lambda);
        ISqlQueryableExpression queryable = queryBase.getSqlBuilder().getQueryable();
        int offset = sqlBuilder.getQueryable().getOrderedCount();
        QuerySqlBuilder querySqlBuilder = new QuerySqlBuilder(getConfig(), factory.queryable(factory.from(queryable, offset)));
        querySqlBuilder.setSelect(factory.select(Collections.singletonList(factory.constString("1")), queryable.getTableClass(), true, false));
        querySqlBuilder.addWhere(where);
        ISqlUnaryExpression exists = factory.unary(SqlOperator.EXISTS, querySqlBuilder.getQueryable());
        if (not) {
            sqlBuilder.addWhere(factory.unary(SqlOperator.NOT, exists));
        }
        else {
            sqlBuilder.addWhere(exists);
        }
    }

    protected void groupBy(LambdaExpression<?> lambda) {
        GroupByVisitor groupByVisitor = new GroupByVisitor(getConfig());
        ISqlExpression expression = groupByVisitor.visit(lambda);
        ISqlGroupByExpression group;
        if (expression instanceof ISqlGroupByExpression) {
            group = (ISqlGroupByExpression) expression;
        }
        else {
            SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
            LinkedHashMap<String, ISqlExpression> map = new LinkedHashMap<>();
            map.put("key", expression);
            group = factory.groupBy(map);
        }
        sqlBuilder.setGroup(group);
    }

    protected void having(LambdaExpression<?> lambda) {
        HavingVisitor havingVisitor = new HavingVisitor(getConfig(), sqlBuilder.getQueryable());
        ISqlExpression expression = havingVisitor.visit(lambda);
        sqlBuilder.addHaving(expression);
    }

    protected void orderBy(LambdaExpression<?> lambda, boolean asc) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        HavingVisitor havingVisitor = new HavingVisitor(getConfig(), sqlBuilder.getQueryable());
        ISqlExpression expression = havingVisitor.visit(lambda);
        sqlBuilder.addOrder(factory.order(expression, asc));
    }

    protected void limit0(long rows) {
        limit0(0, rows);
    }

    protected void limit0(long offset, long rows) {
        sqlBuilder.setLimit(offset, rows);
    }

    protected void singleCheck(boolean single) {
        if (single) {
            throw new RuntimeException("query.select(Func<T1,T2..., R> expr) 不允许传入单个元素, 单元素请使用endSelect");
        }
    }

    public String toSql() {
        return sqlBuilder.getSql();
    }

    protected QuerySqlBuilder boxedQuerySqlBuilder() {
        return new QuerySqlBuilder(getConfig(), getConfig().getSqlExpressionFactory().queryable(sqlBuilder.getQueryable()));
    }

    protected void include(LambdaExpression<?> lambda, LambdaExpression<?> cond, List<IncludeSet> includeSets) {
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression expression = normalVisitor.visit(lambda);
        if (expression instanceof ISqlColumnExpression) {
            ISqlColumnExpression columnExpression = (ISqlColumnExpression) expression;
            if (!columnExpression.getFieldMetaData().hasNavigate()) {
                throw new RuntimeException("include指定的字段需要被@Navigate修饰");
            }
            relationTypeCheck(columnExpression.getFieldMetaData().getNavigateData());
            IncludeSet includeSet;
            if (cond != null) {
                NormalVisitor normalVisitor2 = new NormalVisitor(getConfig());
                ISqlExpression condition = normalVisitor2.visit(cond);
                includeSet = new IncludeSet(columnExpression, condition);
            }
            else {
                includeSet = new IncludeSet(columnExpression);
            }
            includeSets.add(includeSet);
        }
        else {
            throw new RuntimeException("include需要指定一个字段");
        }
    }

    protected void include(LambdaExpression<?> lambda, LambdaExpression<?> cond) {
        include(lambda, cond, sqlBuilder.getIncludeSets());
    }

    protected void include(LambdaExpression<?> lambda) {
        include(lambda, null, sqlBuilder.getIncludeSets());
    }

    protected <R> void includeByCond(LambdaExpression<?> lambda, Action1<IncludeCond<R>> action, List<IncludeSet> includeSets) {
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlExpression expression = normalVisitor.visit(lambda);
        if (expression instanceof ISqlColumnExpression) {
            ISqlColumnExpression columnExpression = (ISqlColumnExpression) expression;
            FieldMetaData fieldMetaData = columnExpression.getFieldMetaData();
            if (!columnExpression.getFieldMetaData().hasNavigate()) {
                throw new RuntimeException("include指定的字段需要被@Navigate修饰");
            }
            relationTypeCheck(columnExpression.getFieldMetaData().getNavigateData());
            Class<R> navigateTargetType = (Class<R>) fieldMetaData.getNavigateData().getNavigateTargetType();
            IncludeCond<R> temp = new IncludeCond<>(new QuerySqlBuilder(getConfig(), getConfig().getSqlExpressionFactory().queryable(navigateTargetType)));
            action.invoke(temp);
            IncludeSet includeSet = new IncludeSet(columnExpression, temp.getSqlBuilder().getQueryable());
            includeSets.add(includeSet);
        }
        else {
            throw new RuntimeException("include需要指定一个字段");
        }
    }

    protected <R> void includeByCond(LambdaExpression<?> lambda, Action1<IncludeCond<R>> action) {
        includeByCond(lambda, action, sqlBuilder.getIncludeSets());
    }

    protected void relationTypeCheck(NavigateData navigateData) {
        RelationType relationType = navigateData.getRelationType();
        switch (relationType) {
            case OneToOne:
            case ManyToOne:
                if (navigateData.isCollectionWrapper()) {
                    throw new SqLinkException(relationType + "不支持集合");
                }
                break;
            case OneToMany:
                if (!navigateData.isCollectionWrapper()) {
                    if (!(List.class.isAssignableFrom(navigateData.getCollectionWrapperType()) || Set.class.isAssignableFrom(navigateData.getCollectionWrapperType()))) {
                        throw new SqLinkException(relationType + "只支持List和Set");
                    }
                    throw new SqLinkException(relationType + "只支持集合");
                }
                break;
            case ManyToMany:
                if (navigateData.getMappingTableType() == IMappingTable.class
                        || navigateData.getSelfMappingFieldName().isEmpty()
                        || navigateData.getTargetMappingFieldName().isEmpty()) {
                    throw new SqLinkException(relationType + "下@Navigate注解的midTable和SelfMapping和TargetMapping字段都不能为空");
                }
                if (!navigateData.isCollectionWrapper()) {
                    if (!(List.class.isAssignableFrom(navigateData.getCollectionWrapperType()) || Set.class.isAssignableFrom(navigateData.getCollectionWrapperType()))) {
                        throw new SqLinkException(relationType + "只支持List和Set");
                    }
                    throw new RuntimeException(relationType + "只支持集合");
                }
                break;
        }
    }

    protected <T> PagedResult<T> toPagedResult0(long pageIndex, long pageSize, Pager pager) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        QuerySqlBuilder boxedQuerySqlBuilder = boxedQuerySqlBuilder();
        //SELECT COUNT(*) ...
        boxedQuerySqlBuilder.setSelect(factory.select(Collections.singletonList(AggregateMethods.count(getConfig(), null)), long.class, true, false));
        LQuery<Long> countQuery = new LQuery<>(boxedQuerySqlBuilder);
        long total = countQuery.toList().get(0);
        QuerySqlBuilder dataQuerySqlBuilder = new QuerySqlBuilder(getConfig(), getSqlBuilder().getQueryable().copy(getConfig()));
        //拷贝Include
        dataQuerySqlBuilder.getIncludeSets().addAll(dataQuerySqlBuilder.getIncludeSets());
        LQuery<T> dataQuery = new LQuery<>(dataQuerySqlBuilder);
        long take = Math.max(pageSize, 1);
        long index = Math.max(pageIndex, 1);
        long offset = (index - 1) * take;
        List<T> list = dataQuery.limit(offset, take).toList();
        return pager.getPagedResult(total, list);
    }

    protected long count0(LambdaExpression<?> lambda) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        List<ISqlExpression> countList;
        if (lambda == null) {
            ISqlTemplateExpression count = AggregateMethods.count(getConfig(), null);
            countList = Collections.singletonList(count);
        }
        else {
            NormalVisitor normalVisitor = new NormalVisitor(getConfig());
            ISqlTemplateExpression count = AggregateMethods.count(getConfig(), normalVisitor.visit(lambda));
            countList = Collections.singletonList(count);
        }
        ISqlQueryableExpression copy = sqlBuilder.getQueryable().copy(getConfig());
        QuerySqlBuilder copyQuerySqlBuilder = new QuerySqlBuilder(getConfig(), copy);
        copyQuerySqlBuilder.setSelect(factory.select(countList, long.class, true, false));
        LQuery<Long> countQuery = new LQuery<>(copyQuerySqlBuilder);
        return countQuery.toList().get(0);
    }

    protected <T extends Number> T sum0(LambdaExpression<?> lambda) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlTemplateExpression sum = AggregateMethods.sum(getConfig(), normalVisitor.visit(lambda));
        List<ISqlExpression> sumList = Collections.singletonList(sum);
        ISqlQueryableExpression copy = sqlBuilder.getQueryable().copy(getConfig());
        QuerySqlBuilder copyQuerySqlBuilder = new QuerySqlBuilder(getConfig(), copy);
        copyQuerySqlBuilder.setSelect(factory.select(sumList, lambda.getReturnType(), true, false));
        LQuery<T> sumQuery = new LQuery<>(copyQuerySqlBuilder);
        return sumQuery.toList().get(0);
    }

    protected BigDecimal avg0(LambdaExpression<?> lambda) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlTemplateExpression avg = AggregateMethods.avg(getConfig(), normalVisitor.visit(lambda));
        List<ISqlExpression> avgList = Collections.singletonList(avg);
        ISqlQueryableExpression copy = sqlBuilder.getQueryable().copy(getConfig());
        QuerySqlBuilder avgQuerySqlBuilder = new QuerySqlBuilder(getConfig(), copy);
        avgQuerySqlBuilder.setSelect(factory.select(avgList, BigDecimal.class, true, false));
        LQuery<BigDecimal> avgQuery = new LQuery<>(avgQuerySqlBuilder);
        return avgQuery.toList().get(0);
    }

    protected <T extends Number> T max0(LambdaExpression<?> lambda) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlTemplateExpression max = AggregateMethods.max(getConfig(), normalVisitor.visit(lambda));
        List<ISqlExpression> maxList = Collections.singletonList(max);
        ISqlQueryableExpression copy = sqlBuilder.getQueryable().copy(getConfig());
        QuerySqlBuilder maxQuerySqlBuilder = new QuerySqlBuilder(getConfig(), copy);
        maxQuerySqlBuilder.setSelect(factory.select(maxList, lambda.getReturnType(), true, false));
        LQuery<T> maxQuery = new LQuery<>(maxQuerySqlBuilder);
        return maxQuery.toList().get(0);
    }

    protected <T extends Number> T min0(LambdaExpression<?> lambda) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NormalVisitor normalVisitor = new NormalVisitor(getConfig());
        ISqlTemplateExpression min = AggregateMethods.min(getConfig(), normalVisitor.visit(lambda));
        List<ISqlExpression> minList = Collections.singletonList(min);
        ISqlQueryableExpression copy = sqlBuilder.getQueryable().copy(getConfig());
        QuerySqlBuilder minQuerySqlBuilder = new QuerySqlBuilder(getConfig(), copy);
        minQuerySqlBuilder.setSelect(factory.select(minList, lambda.getReturnType(), true, false));
        LQuery<T> minQuery = new LQuery<>(minQuerySqlBuilder);
        return minQuery.toList().get(0);
    }
}
