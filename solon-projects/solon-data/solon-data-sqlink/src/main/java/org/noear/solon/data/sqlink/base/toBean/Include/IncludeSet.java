package org.noear.solon.data.sqlink.base.toBean.Include;



import org.noear.solon.data.sqlink.base.expression.ISqlColumnExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;

import java.util.ArrayList;
import java.util.List;

public class IncludeSet
{
    private final ISqlColumnExpression columnExpression;
    private final ISqlExpression cond;
    private final List<IncludeSet> includeSets = new ArrayList<>();

    public IncludeSet(ISqlColumnExpression columnExpression, ISqlExpression cond)
    {
        this.columnExpression = columnExpression;
        this.cond = cond;
    }

    public IncludeSet(ISqlColumnExpression columnExpression)
    {
        this(columnExpression,null);
    }

    public ISqlColumnExpression getColumnExpression()
    {
        return columnExpression;
    }

    public ISqlExpression getCond()
    {
        return cond;
    }

    public boolean hasCond()
    {
        return cond != null;
    }

    public List<IncludeSet> getIncludeSets()
    {
        return includeSets;
    }

    public IncludeSet getLastIncludeSet()
    {
        return includeSets.get(includeSets.size() - 1);
    }
}
