package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlCollectedValueExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SqlCollectedValueExpression implements ISqlCollectedValueExpression
{
    private final Collection<Object> collection;
    private String delimiter = ",";

    public SqlCollectedValueExpression(Collection<Object> collection)
    {
        this.collection = collection;
    }

    @Override
    public Collection<Object> getCollection()
    {
        return collection;
    }

    @Override
    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }

    @Override
    public String getDelimiter()
    {
        return delimiter;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        List<String> strings = new ArrayList<>(getCollection().size());
        for (Object obj : getCollection())
        {
            strings.add("?");
            if (values != null) values.add(obj);
        }
        return String.join(getDelimiter(), strings);
    }
}
