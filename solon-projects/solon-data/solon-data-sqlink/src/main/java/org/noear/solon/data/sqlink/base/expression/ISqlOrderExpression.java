package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;

public interface ISqlOrderExpression extends ISqlExpression
{
    ISqlExpression getExpression();

    boolean isAsc();

    @Override
    default ISqlOrderExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.order(getExpression().copy(config), isAsc());
    }
}
