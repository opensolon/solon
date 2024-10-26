package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.IDialect;

public interface ISqlAsExpression extends ISqlExpression
{
    ISqlExpression getExpression();

    String getAsName();

    @Override
    default ISqlAsExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.as(getExpression().copy(config), getAsName());
    }
}
