package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.IDialect;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;

public interface ISqlColumnExpression extends ISqlExpression
{
    PropertyMetaData getPropertyMetaData();

    int getTableIndex();

    @Override
    default ISqlColumnExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.column(getPropertyMetaData(), getTableIndex());
    }
}
