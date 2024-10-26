package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.*;

import java.util.List;

public class SqlJoinExpression implements ISqlJoinExpression
{
    protected final JoinType joinType;
    protected final ISqlTableExpression joinTable;
    protected final ISqlExpression conditions;
    protected final int index;

    protected SqlJoinExpression(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, int index)
    {
        this.joinType = joinType;
        this.joinTable = joinTable;
        this.conditions = conditions;
        this.index = index;
    }

    @Override
    public JoinType getJoinType()
    {
        return joinType;
    }

    @Override
    public ISqlTableExpression getJoinTable()
    {
        return joinTable;
    }

    @Override
    public ISqlExpression getConditions()
    {
        return conditions;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        String t = "t" + getIndex();
        return getJoinType().getJoin() + " " + (getJoinTable() instanceof ISqlRealTableExpression ? getJoinTable().getSqlAndValue(config, values) : "(" + getJoinTable().getSqlAndValue(config, values) + ")") + " AS " + config.getDisambiguation().disambiguation(t) + " ON " + getConditions().getSqlAndValue(config, values);
    }
}
