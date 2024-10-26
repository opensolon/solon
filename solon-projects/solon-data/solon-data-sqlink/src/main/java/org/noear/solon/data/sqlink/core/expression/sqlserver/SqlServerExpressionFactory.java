package org.noear.solon.data.sqlink.core.expression.sqlserver;


import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.expression.impl.DefaultSqlExpressionFactory;

public class SqlServerExpressionFactory extends DefaultSqlExpressionFactory
{
    @Override
    public ISqlLimitExpression limit()
    {
        return new SqlServerLimitExpression();
    }

    @Override
    public ISqlQueryableExpression queryable(ISqlSelectExpression select, ISqlFromExpression from, ISqlJoinsExpression joins, ISqlWhereExpression where, ISqlGroupByExpression groupBy, ISqlHavingExpression having, ISqlOrderByExpression orderBy, ISqlLimitExpression limit)
    {
        return new SqlServerQueryableExpression(select, from, joins, where, groupBy, having, orderBy, limit);
    }
}
