package org.noear.solon.data.sqlink.core.expression.sqlserver;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.impl.SqlLimitExpression;

import java.util.List;

public class SqlServerLimitExpression extends SqlLimitExpression
{
    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (hasRowsAndOffset())
        {
            return String.format("OFFSET %d ROWS FETCH NEXT %d ROWS ONLY", offset, rows);
        }
        return "";
    }
}
