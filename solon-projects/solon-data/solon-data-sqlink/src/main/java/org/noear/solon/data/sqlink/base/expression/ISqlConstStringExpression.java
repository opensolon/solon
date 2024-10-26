package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

public interface ISqlConstStringExpression extends ISqlExpression
{
    String getString();

    @Override
    default ISqlConstStringExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.constString(getString());
    }
}
