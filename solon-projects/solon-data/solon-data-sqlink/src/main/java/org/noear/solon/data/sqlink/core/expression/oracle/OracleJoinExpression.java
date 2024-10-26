package org.noear.solon.data.sqlink.core.expression.oracle;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlRealTableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTableExpression;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.base.expression.impl.SqlJoinExpression;

import java.util.List;

public class OracleJoinExpression extends SqlJoinExpression
{
    protected OracleJoinExpression(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, int index)
    {
        super(joinType, joinTable, conditions, index);
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        return joinType.getJoin() + " " + (joinTable instanceof ISqlRealTableExpression ? joinTable.getSqlAndValue(config, values) : "(" + joinTable.getSqlAndValue(config, values) + ")") + " t" + index + " ON " + conditions.getSqlAndValue(config, values);
    }
}
