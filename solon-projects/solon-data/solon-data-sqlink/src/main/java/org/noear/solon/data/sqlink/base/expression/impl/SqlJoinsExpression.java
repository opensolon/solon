package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlJoinExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlJoinsExpression;

import java.util.ArrayList;
import java.util.List;

public class SqlJoinsExpression implements ISqlJoinsExpression
{
    private final List<ISqlJoinExpression> joins = new ArrayList<>();

    @Override
    public void addJoin(ISqlJoinExpression join)
    {
        joins.add(join);
    }

    public List<ISqlJoinExpression> getJoins()
    {
        return joins;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (getJoins().isEmpty()) return "";
        List<String> strings = new ArrayList<>(getJoins().size());
        for (ISqlJoinExpression join : getJoins())
        {
            strings.add(join.getSqlAndValue(config,values));
        }
        return String.join(" ", strings);
    }
}
