package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlConstStringExpression;

import java.util.List;

public class SqlConstStringExpression implements ISqlConstStringExpression
{
    private final String string;

    public SqlConstStringExpression(String string)
    {
        this.string = string;
    }

    public String getString()
    {
        return string;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        return getString();
    }
}
