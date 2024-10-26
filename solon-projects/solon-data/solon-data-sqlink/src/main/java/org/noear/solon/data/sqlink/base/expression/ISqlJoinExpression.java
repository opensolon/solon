package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

public interface ISqlJoinExpression extends ISqlExpression
{
    JoinType getJoinType();

    ISqlTableExpression getJoinTable();

    ISqlExpression getConditions();

    int getIndex();

    @Override
    default ISqlJoinExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.join(getJoinType(), getJoinTable().copy(config), getConditions().copy(config), getIndex());
    }
}
