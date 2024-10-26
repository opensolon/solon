package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;

public interface ISqlSetExpression extends ISqlExpression
{
    ISqlColumnExpression getColumn();

    ISqlExpression getValue();

    @Override
    default ISqlSetExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.set(getColumn().copy(config), getValue().copy(config));
    }
}
