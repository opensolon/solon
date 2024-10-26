package org.noear.solon.data.sqlink.core.api.crud.create;


import org.noear.solon.data.sqlink.base.IConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectInsert<T> extends InsertBase
{
    private final List<T> tObjects = new ArrayList<>();
    private final Class<T> tableType;

    public ObjectInsert(IConfig config, Class<T> tableType)
    {
        super(config);
        this.tableType = tableType;
    }

    public ObjectInsert<T> insert(T t)
    {
        tObjects.add(t);
        return this;
    }

    public ObjectInsert<T> insert(Collection<T> ts)
    {
        tObjects.addAll(ts);
        return this;
    }

    @Override
    protected List<T> getObjects()
    {
        return tObjects;
    }

    @Override
    protected Class<T> getTableType()
    {
        return tableType;
    }
}
