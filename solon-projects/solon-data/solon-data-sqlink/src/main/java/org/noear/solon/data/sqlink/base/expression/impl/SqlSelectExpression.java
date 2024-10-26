package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSelectExpression;

import java.util.ArrayList;
import java.util.List;

public class SqlSelectExpression implements ISqlSelectExpression
{
    protected List<ISqlExpression> columns;
    protected boolean distinct = false;
    protected Class<?> target;
    protected boolean isSingle;

    SqlSelectExpression(List<ISqlExpression> columns, Class<?> target, boolean isSingle, boolean isDistinct)
    {
        this.columns = columns;
        this.target = target;
        this.isSingle = isSingle;
        this.distinct = isDistinct;
    }

    @Override
    public List<ISqlExpression> getColumns()
    {
        return columns;
    }

    public void setColumns(List<ISqlExpression> columns)
    {
        this.columns = columns;
    }

    @Override
    public boolean isDistinct()
    {
        return distinct;
    }

    public void setDistinct(boolean distinct)
    {
        this.distinct = distinct;
    }

    @Override
    public Class<?> getTarget()
    {
        return target;
    }

    public void setTarget(Class<?> target)
    {
        this.target = target;
    }

    @Override
    public boolean isSingle()
    {
        return isSingle;
    }

    public void setSingle(boolean single)
    {
        isSingle = single;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        List<String> strings = new ArrayList<>(getColumns().size());
        for (ISqlExpression sqlExpression : getColumns())
        {
            strings.add(sqlExpression.getSqlAndValue(config, values));
        }
        String col = String.join(",", strings);
        List<String> result = new ArrayList<>();
        result.add("SELECT");
        if (isDistinct())
        {
            result.add("DISTINCT");
        }
        result.add(col);
        return String.join(" ", result);
    }
}
