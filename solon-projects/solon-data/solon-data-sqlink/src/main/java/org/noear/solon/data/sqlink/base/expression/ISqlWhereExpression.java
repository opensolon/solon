package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

public interface ISqlWhereExpression extends ISqlExpression
{
    default boolean isEmpty()
    {
        return getConditions().isEmpty();
    }

    ISqlConditionsExpression getConditions();

    void addCondition(ISqlExpression condition);

    @Override
    default ISqlWhereExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.where(getConditions().copy(config));
    }
}
