package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlLimitExpression;

import java.util.List;

public class SqlLimitExpression implements ISqlLimitExpression
{
    protected long offset, rows;

    public long getOffset()
    {
        return offset;
    }

    public long getRows()
    {
        return rows;
    }

    public void setOffset(long offset)
    {
        this.offset = offset;
    }

    public void setRows(long rows)
    {
        this.rows = rows;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (getRows() > 0)
        {
            if (getOffset() > 0)
            {
                return String.format("LIMIT %d OFFSET %d", getRows(), getOffset());
            }
            else
            {
                return String.format("LIMIT %d", getRows());
            }
        }
        return "";
    }
}
