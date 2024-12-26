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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.doGetAsName;
import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.getFirst;

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
        String first = getFirst(table.getMainTableClass());
        AsName asName = doGetAsName(first,queryable.getFrom(),queryable.getJoins());
        ISqlJoinExpression join = factory.join(joinType, table, conditions, asName);
        queryable.addJoin(join);
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
        MetaData metaData = MetaDataCache.getMetaData(c);
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        ISqlFromExpression from = queryable.getFrom();
        ISqlJoinsExpression joins = queryable.getJoins();
        List<ISqlExpression> expressions = new ArrayList<>();
        if (from.getSqlTableExpression().getMainTableClass() == c) {
            for (FieldMetaData notIgnoreProperty : metaData.getNotIgnorePropertys()) {
                expressions.add(factory.column(notIgnoreProperty, from.getAsName()));
            }
        }
        else if (joins.getJoins().stream().anyMatch(join -> join.getJoinTable().getMainTableClass() == c)) {
            for (ISqlJoinExpression join : joins.getJoins()) {
                if (join.getJoinTable().getMainTableClass() == c) {
                    for (FieldMetaData notIgnoreProperty : metaData.getNotIgnorePropertys()) {
                        expressions.add(factory.column(notIgnoreProperty, join.getAsName()));
                    }
                    break;
                }
            }
        }
        else {
            GOTO:
            for (FieldMetaData sel : metaData.getNotIgnorePropertys()) {
                MetaData mainTableMetaData = MetaDataCache.getMetaData(from.getSqlTableExpression().getMainTableClass());
                for (FieldMetaData noi : mainTableMetaData.getNotIgnorePropertys()) {
                    if (noi.getColumn().equals(sel.getColumn()) && noi.getType().equals(sel.getType())) {
                        expressions.add(factory.column(sel, from.getAsName()));
                        break GOTO;
                    }
                }
                for (ISqlJoinExpression join : joins.getJoins()) {
                    MetaData joinTableMetaData = MetaDataCache.getMetaData(join.getJoinTable().getMainTableClass());
                    for (FieldMetaData noi : joinTableMetaData.getNotIgnorePropertys()) {
                        if (noi.getColumn().equals(sel.getColumn()) && noi.getType().equals(sel.getType())) {
                            expressions.add(factory.column(sel, join.getAsName()));
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
