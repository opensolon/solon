package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;

public interface ISqlBinaryExpression extends ISqlExpression
{
    ISqlExpression getLeft();

    ISqlExpression getRight();

    SqlOperator getOperator();

    @Override
    default ISqlBinaryExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.binary(getOperator(), getLeft().copy(config), getRight().copy(config));
    }
}
