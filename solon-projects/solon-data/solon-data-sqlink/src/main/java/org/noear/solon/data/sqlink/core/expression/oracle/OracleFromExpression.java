package org.noear.solon.data.sqlink.core.expression.oracle;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlRealTableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTableExpression;
import org.noear.solon.data.sqlink.base.expression.impl.SqlFromExpression;

import java.util.List;

public class OracleFromExpression extends SqlFromExpression
{
    public OracleFromExpression(ISqlTableExpression sqlTableExpression, int index)
    {
        super(sqlTableExpression, index);
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (isEmptyTable()) return "FROM \"DUAL\"";
        String sql;
        if (sqlTableExpression instanceof ISqlRealTableExpression)
        {
            sql = sqlTableExpression.getSqlAndValue(config, values);
        }
        else
        {
            sql = "(" + sqlTableExpression.getSqlAndValue(config, values) + ")";
        }
        return "FROM " + sql + " t" + index;
    }
}
