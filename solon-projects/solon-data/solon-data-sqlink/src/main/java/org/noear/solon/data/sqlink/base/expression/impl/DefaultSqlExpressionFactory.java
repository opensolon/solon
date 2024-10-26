package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;

import java.util.Collection;
import java.util.List;

public class DefaultSqlExpressionFactory extends SqlExpressionFactory
{

    @Override
    public ISqlAsExpression as(ISqlExpression expression, String asName)
    {
        return new SqlAsExpression(expression, asName);
    }

    @Override
    public ISqlColumnExpression column(PropertyMetaData propertyMetaData, int tableIndex)
    {
        return new SqlColumnExpression(propertyMetaData, tableIndex);
    }

    @Override
    public ISqlConditionsExpression condition()
    {
        return new SqlConditionsExpression();
    }

    @Override
    public ISqlFromExpression from(ISqlTableExpression sqlTable, int index)
    {
        return new SqlFromExpression(sqlTable, index);
    }

    @Override
    public ISqlGroupByExpression groupBy()
    {
        return new SqlGroupByExpression();
    }

    @Override
    public ISqlHavingExpression having()
    {
        return new SqlHavingExpression(condition());
    }

    @Override
    public ISqlJoinExpression join(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, int index)
    {
        return new SqlJoinExpression(joinType, joinTable, conditions, index);
    }

    @Override
    public ISqlJoinsExpression Joins()
    {
        return new SqlJoinsExpression();
    }

    @Override
    public ISqlLimitExpression limit()
    {
        return new SqlLimitExpression();
    }

    @Override
    public ISqlOrderByExpression orderBy()
    {
        return new SqlOrderByExpression();
    }

    @Override
    public ISqlOrderExpression order(ISqlExpression expression, boolean asc)
    {
        return new SqlOrderExpression(expression, asc);
    }

    @Override
    public ISqlQueryableExpression queryable(ISqlSelectExpression select, ISqlFromExpression from, ISqlJoinsExpression joins, ISqlWhereExpression where, ISqlGroupByExpression groupBy, ISqlHavingExpression having, ISqlOrderByExpression orderBy, ISqlLimitExpression limit)
    {
        return new SqlQueryableExpression(select, from, joins, where, groupBy, having, orderBy, limit);
    }

    @Override
    public ISqlRealTableExpression table(Class<?> tableClass)
    {
        return new SqlRealTableExpression(tableClass);
    }

    @Override
    public ISqlSelectExpression select(List<ISqlExpression> column, Class<?> target, boolean isSingle, boolean isDistinct)
    {
        return new SqlSelectExpression(column, target, isSingle, isDistinct);
    }

    @Override
    public ISqlWhereExpression where(ISqlConditionsExpression conditions)
    {
        return new SqlWhereExpression(conditions);
    }

    @Override
    public ISqlSetExpression set(ISqlColumnExpression column, ISqlExpression value)
    {
        return new SqlSetExpression(column, value);
    }

    @Override
    public ISqlSingleValueExpression value(Object value)
    {
        return new SqlSingleValueExpression(value);
    }

    @Override
    public ISqlCollectedValueExpression value(Collection<Object> value)
    {
        return new SqlCollectedValueExpression(value);
    }

    @Override
    public ISqlTemplateExpression template(List<String> templates, List<? extends ISqlExpression> expressions)
    {
        return new SqlTemplateExpression(templates, expressions);
    }

    @Override
    public ISqlBinaryExpression binary(SqlOperator operator, ISqlExpression left, ISqlExpression right)
    {
        return new SqlBinaryExpression(operator, left, right);
    }

    @Override
    public ISqlUnaryExpression unary(SqlOperator operator, ISqlExpression expression)
    {
        return new SqlUnaryExpression(operator, expression);
    }

    @Override
    public ISqlParensExpression parens(ISqlExpression expression)
    {
        return new SqlParensExpression(expression);
    }

    @Override
    public ISqlConstStringExpression constString(String s)
    {
        return new SqlConstStringExpression(s);
    }

    @Override
    public ISqlSetsExpression sets()
    {
        return new SqlSetsExpression();
    }

    @Override
    public ISqlTypeExpression type(Class<?> c)
    {
        return new SqlTypeExpression(c);
    }
}
