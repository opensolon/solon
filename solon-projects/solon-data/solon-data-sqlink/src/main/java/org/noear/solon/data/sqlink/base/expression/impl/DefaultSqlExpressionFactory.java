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
package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;

import java.util.Collection;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class DefaultSqlExpressionFactory implements SqlExpressionFactory {
    @Override
    public ISqlAsExpression as(ISqlExpression expression, String asName) {
        return new SqlAsExpression(expression, asName);
    }

    @Override
    public ISqlColumnExpression column(FieldMetaData fieldMetaData, String tableAsName) {
        return new SqlColumnExpression(fieldMetaData, tableAsName);
    }

    @Override
    public ISqlConditionsExpression condition() {
        return new SqlConditionsExpression();
    }

    @Override
    public ISqlFromExpression from(ISqlTableExpression sqlTable, String asName) {
        return new SqlFromExpression(sqlTable, asName);
    }

    @Override
    public ISqlGroupByExpression groupBy() {
        return new SqlGroupByExpression();
    }

    @Override
    public ISqlHavingExpression having() {
        return new SqlHavingExpression(condition());
    }

    @Override
    public ISqlJoinExpression join(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, String asName) {
        return new SqlJoinExpression(joinType, joinTable, conditions, asName);
    }

    @Override
    public ISqlJoinsExpression Joins() {
        return new SqlJoinsExpression();
    }

    @Override
    public ISqlLimitExpression limit() {
        return new SqlLimitExpression();
    }

    @Override
    public ISqlOrderByExpression orderBy() {
        return new SqlOrderByExpression();
    }

    @Override
    public ISqlOrderExpression order(ISqlExpression expression, boolean asc) {
        return new SqlOrderExpression(expression, asc);
    }

    @Override
    public ISqlQueryableExpression queryable(ISqlSelectExpression select, ISqlFromExpression from, ISqlJoinsExpression joins, ISqlWhereExpression where, ISqlGroupByExpression groupBy, ISqlHavingExpression having, ISqlOrderByExpression orderBy, ISqlLimitExpression limit, ISqlUnionsExpression union) {
        return new SqlQueryableExpression(select, from, joins, where, groupBy, having, orderBy, limit,union);
    }

    @Override
    public ISqlRealTableExpression table(Class<?> tableClass) {
        return new SqlRealTableExpression(tableClass);
    }

    @Override
    public ISqlSelectExpression select(List<ISqlExpression> column, Class<?> target, boolean isSingle, boolean isDistinct) {
        return new SqlSelectExpression(column, target, isSingle, isDistinct);
    }

    @Override
    public ISqlWhereExpression where(ISqlConditionsExpression conditions) {
        return new SqlWhereExpression(conditions);
    }

    @Override
    public ISqlSetExpression set(ISqlColumnExpression column, ISqlExpression value) {
        return new SqlSetExpression(column, value);
    }

    @Override
    public ISqlSingleValueExpression value(Object value) {
        return new SqlSingleValueExpression(value);
    }

    @Override
    public ISqlCollectedValueExpression value(Collection<?> value) {
        return new SqlCollectedValueExpression(value);
    }

    @Override
    public ISqlTemplateExpression template(List<String> templates, List<? extends ISqlExpression> expressions) {
        return new SqlTemplateExpression(templates, expressions);
    }

    @Override
    public ISqlBinaryExpression binary(SqlOperator operator, ISqlExpression left, ISqlExpression right) {
        return new SqlBinaryExpression(operator, left, right);
    }

    @Override
    public ISqlUnaryExpression unary(SqlOperator operator, ISqlExpression expression) {
        return new SqlUnaryExpression(operator, expression);
    }

    @Override
    public ISqlParensExpression parens(ISqlExpression expression) {
        return new SqlParensExpression(expression);
    }

    @Override
    public ISqlConstStringExpression constString(String s) {
        return new SqlConstStringExpression(s);
    }

    @Override
    public ISqlSetsExpression sets() {
        return new SqlSetsExpression();
    }

    @Override
    public ISqlTypeExpression type(Class<?> c) {
        return new SqlTypeExpression(c);
    }

    @Override
    public ISqlWithExpression with(ISqlQueryableExpression queryable, String name) {
        return new SqlWithExpression(queryable, name);
    }

    @Override
    public ISqlWithsExpression withs() {
        return new SqlWithsExpression();
    }

    @Override
    public ISqlUnionExpression union(ISqlQueryableExpression queryable, boolean all) {
        return new SqlUnionExpression(queryable, all);
    }

    @Override
    public ISqlUnionsExpression unions() {
        return new SqlUnionsExpression();
    }
}
