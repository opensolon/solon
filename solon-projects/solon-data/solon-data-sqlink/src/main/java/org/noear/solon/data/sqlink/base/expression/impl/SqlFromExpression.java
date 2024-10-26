package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlFromExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlRealTableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTableExpression;

import java.util.List;

public class SqlFromExpression implements ISqlFromExpression
{
    protected final ISqlTableExpression sqlTableExpression;
    protected final int index;

    public SqlFromExpression(ISqlTableExpression sqlTableExpression, int index)
    {
        this.sqlTableExpression = sqlTableExpression;
        this.index = index;
    }

    @Override
    public ISqlTableExpression getSqlTableExpression()
    {
        return sqlTableExpression;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (isEmptyTable()) return "";
        String sql;
        if (getSqlTableExpression() instanceof ISqlRealTableExpression)
        {
            sql = getSqlTableExpression().getSqlAndValue(config, values);
        }
        else
        {
            sql = "(" + getSqlTableExpression().getSqlAndValue(config, values) + ")";
        }
        String t = "t" + getIndex();
        return "FROM " + sql + " AS " + config.getDisambiguation().disambiguation(t);
    }
}
