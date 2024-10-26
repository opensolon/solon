package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlGroupByExpression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SqlGroupByExpression implements ISqlGroupByExpression
{
    protected final LinkedHashMap<String, ISqlExpression> columns = new LinkedHashMap<>();

    public void setColumns(LinkedHashMap<String, ISqlExpression> columns)
    {
        this.columns.putAll(columns);
    }

    public LinkedHashMap<String, ISqlExpression> getColumns()
    {
        return columns;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (getColumns().isEmpty()) return "";
        List<String> strings = new ArrayList<>();
        for (ISqlExpression column : getColumns().values())
        {
            strings.add(column.getSqlAndValue(config, values));
        }
        return "GROUP BY " + String.join(",", strings);
    }
}
