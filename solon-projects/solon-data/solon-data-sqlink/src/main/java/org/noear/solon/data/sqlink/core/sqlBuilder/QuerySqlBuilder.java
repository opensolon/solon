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
package org.noear.solon.data.sqlink.core.sqlBuilder;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeSet;
import org.noear.solon.data.sqlink.core.visitor.ExpressionUtil;

import java.util.*;
import java.util.stream.Collectors;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.getAsName;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class QuerySqlBuilder implements ISqlBuilder {
    private final SqLinkConfig config;
    private ISqlQueryableExpression queryable;
    private final List<IncludeSet> includeSets = new ArrayList<>();

//    public QuerySqlBuilder(IConfig config, Class<?> target, int offset)
//    {
//        this(config, config.getSqlExpressionFactory().table(target), offset);
//    }
//
//    public QuerySqlBuilder(IConfig config, Class<?> target)
//    {
//        this(config, config.getSqlExpressionFactory().table(target), 0);
//    }
//
//    public QuerySqlBuilder(IConfig config, ISqlTableExpression target)
//    {
//        this(config, target, 0);
//    }
//
//    public QuerySqlBuilder(IConfig config, ISqlTableExpression target, int offset)
//    {
//        this.config = config;
//        SqlExpressionFactory factory = config.getSqlExpressionFactory();
//        queryable = factory.queryable(factory.from(target, offset));
//    }

    public QuerySqlBuilder(SqLinkConfig config, ISqlQueryableExpression queryable) {
        this.config = config;
        this.queryable = queryable;
    }

    public void addWhere(ISqlExpression cond) {
        queryable.addWhere(cond);
    }

    public void addOrWhere(ISqlExpression cond) {
        if (queryable.getWhere().isEmpty()) {
            addWhere(cond);
        }
        else {
            SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
            addWhere(factory.unary(SqlOperator.OR, cond));
        }
    }

    public void addJoin(JoinType joinType, ISqlTableExpression table, ISqlExpression conditions) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        String asName = getAsName(table.getMainTableClass());
        asName = doGetAsName(asName);
        ISqlJoinExpression join = factory.join(joinType, table, conditions, asName);
        queryable.addJoin(join);
    }

    private String doGetAsName(String as) {
        Set<String> asNames = new HashSet<>();
        String asName = queryable.getFrom().getAsName();
        asNames.add(asName);
        queryable.getJoins().getJoins().forEach(join -> asNames.add(join.getAsName()));
        return doGetAsName(asNames, as, 0);
    }

    private String doGetAsName(Set<String> asNameSet,String as, int offset) {
        String next = offset == 0 ? as : as + offset;
        if (asNameSet.contains(next)) {
            return doGetAsName(asNameSet,as, offset + 1);
        }
        else {
            return next;
        }
    }

    public void setGroup(ISqlGroupByExpression group) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        queryable.setGroup(group);
    }

    public void addHaving(ISqlExpression cond) {
        queryable.addHaving(cond);
    }

    public void addOrder(ISqlOrderExpression order) {
        queryable.addOrder(order);
    }

    public void setSelect(ISqlSelectExpression select) {
        queryable.setSelect(select);
    }

    public void setSelect(Class<?> c) {
        List<Class<?>> orderedClass = getOrderedClass();
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        MetaData metaData = MetaDataCache.getMetaData(c);
        List<ISqlExpression> expressions = new ArrayList<>();
        if (orderedClass.contains(c)) {
            String as = metaData.getTableName().substring(0, 1).toLowerCase();
            for (FieldMetaData notIgnoreProperty : metaData.getNotIgnorePropertys()) {
                expressions.add(factory.column(notIgnoreProperty, as));
            }
        }
        else {
            for (FieldMetaData sel : metaData.getNotIgnorePropertys()) {
                GOTO:
                for (MetaData data : MetaDataCache.getMetaData(getOrderedClass())) {
                    String as = ExpressionUtil.getAsName(data.getType());
                    for (FieldMetaData noi : data.getNotIgnorePropertys()) {
                        if (noi.getColumn().equals(sel.getColumn()) && noi.getType().equals(sel.getType())) {
                            expressions.add(factory.column(sel, as));
                            break GOTO;
                        }
                    }
                }
            }
        }
        queryable.setSelect(factory.select(expressions, c));
    }

    public void setLimit(long offset, long rows) {
        queryable.setLimit(offset, rows);
    }

    public void setDistinct(boolean distinct) {
        queryable.setDistinct(distinct);
    }

    @Override
    public SqLinkConfig getConfig() {
        return config;
    }

    @Override
    public String getSql() {
        return queryable.getSql(config);
    }

    @Override
    public String getSqlAndValue(List<SqlValue> values) {
        return queryable.getSqlAndValue(config, values);
    }

//    public String getSqlAndValueAndFirst(List<Object> values)
//    {
//        if (isChanged)
//        {
//            return queryable.getSqlAndValueAndFirst(config, values);
//        }
//        else
//        {
//            SqlTableExpression sqlTableExpression = queryable.getFrom().getSqlTableExpression();
//            if (sqlTableExpression instanceof SqlRealTableExpression)
//            {
//                return queryable.getSqlAndValueAndFirst(config, values);
//            }
//            else
//            {
//                SqlQueryableExpression tableExpression = (SqlQueryableExpression) sqlTableExpression;
//                return tableExpression.getSqlAndValueAndFirst(config, values);
//            }
//        }
//    }

    public List<Class<?>> getOrderedClass() {
        return queryable.getOrderedClass();
    }

    public List<FieldMetaData> getMappingData() {
        return queryable.getMappingData();
    }

    public boolean isSingle() {
        return queryable.getSelect().isSingle();
    }

    public <T> Class<T> getTargetClass() {
        return (Class<T>) queryable.getMainTableClass();
    }

    public ISqlQueryableExpression getQueryable() {
        return queryable;
    }

    public void setQueryable(ISqlQueryableExpression queryable) {
        this.queryable = queryable;
    }

    public List<IncludeSet> getIncludeSets() {
        return includeSets;
    }

    public IncludeSet getLastIncludeSet() {
        return includeSets.get(includeSets.size() - 1);
    }
}
