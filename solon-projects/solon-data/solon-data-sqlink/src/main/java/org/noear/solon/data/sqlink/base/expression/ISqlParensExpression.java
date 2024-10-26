package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

public interface ISqlParensExpression extends ISqlExpression
{
    ISqlExpression getExpression();

    @Override
    default ISqlParensExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.parens(getExpression().copy(config));
    }
}
