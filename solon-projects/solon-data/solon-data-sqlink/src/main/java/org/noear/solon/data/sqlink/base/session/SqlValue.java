package org.noear.solon.data.sqlink.base.session;

import java.util.List;

public class SqlValue
{
    private final Class<?> type;
    private final List<Object> values;

    public SqlValue(Class<?> type, List<Object> values)
    {
        this.type = type;
        this.values = values;
    }

    public Class<?> getType()
    {
        return type;
    }

    public List<Object> getValues()
    {
        return values;
    }

    @Override
    public String toString()
    {
        return "SqlValue{" +
                "type=" + type +
                ", values=" + values +
                '}';
    }
}
