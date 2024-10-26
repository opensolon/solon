package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlParensExpression;

import java.util.List;

public class SqlParensExpression implements ISqlParensExpression
{
    private final ISqlExpression expression;

    public SqlParensExpression(ISqlExpression expression)
    {
        this.expression = expression;
    }

    public ISqlExpression getExpression()
    {
        return expression;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        return "(" + getExpression().getSqlAndValue(config, values) + ")";
    }
}
