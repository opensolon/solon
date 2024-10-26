package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlTypeExpression;

import java.util.List;

public class SqlTypeExpression implements ISqlTypeExpression
{
    private final Class<?> type;

    public SqlTypeExpression(Class<?> type)
    {
        this.type = type;
    }

    @Override
    public Class<?> getType()
    {
        return type;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        return type.getSimpleName();
    }

    @Override
    public ISqlTypeExpression copy(IConfig config)
    {
        return config.getSqlExpressionFactory().type(type);
    }
}
