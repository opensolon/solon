package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;

import java.util.List;

public interface ISqlConditionsExpression extends ISqlExpression
{
    List<ISqlExpression> getConditions();

    default void addCondition(ISqlExpression cond)
    {
        getConditions().add(cond);
    }

    default boolean isEmpty()
    {
        return getConditions().isEmpty();
    }

    @Override
    default ISqlConditionsExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlConditionsExpression newConditions = factory.condition();
        for (ISqlExpression condition : getConditions())
        {
            newConditions.getConditions().add(condition.copy(config));
        }
        return newConditions;
    }
}
