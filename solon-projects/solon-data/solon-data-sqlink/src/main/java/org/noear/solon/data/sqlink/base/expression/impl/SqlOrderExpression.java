package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlOrderExpression;

import java.util.List;

public class SqlOrderExpression implements ISqlOrderExpression
{
    private final ISqlExpression expression;
    private final boolean asc;

    public SqlOrderExpression(ISqlExpression expression, boolean asc)
    {
        this.expression = expression;
        this.asc = asc;
    }

    @Override
    public ISqlExpression getExpression()
    {
        return expression;
    }

    @Override
    public boolean isAsc()
    {
        return asc;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        return getExpression().getSqlAndValue(config, values) + " " + (isAsc() ? "ASC" : "DESC");
    }

}
