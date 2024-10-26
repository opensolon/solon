package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

public interface ISqlUnaryExpression extends ISqlExpression
{
    SqlOperator getOperator();

    ISqlExpression getExpression();

    @Override
    default ISqlUnaryExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.unary(getOperator(), getExpression().copy(config));
    }
}
