package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

public interface ISqlHavingExpression extends ISqlExpression
{
    ISqlConditionsExpression getConditions();

    void addCond(ISqlExpression condition);

    default boolean isEmpty()
    {
        return getConditions().isEmpty();
    }

    @Override
    default ISqlHavingExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlHavingExpression having = factory.having();
        ISqlConditionsExpression conditions = getConditions();
        for (ISqlExpression condition : conditions.getConditions())
        {
            having.addCond(condition);
        }
        return having;
    }
}
