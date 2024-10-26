package org.noear.solon.data.sqlink.core.api.client;


import org.noear.solon.data.sqlink.core.api.crud.create.ObjectInsert;
import org.noear.solon.data.sqlink.core.api.crud.delete.LDelete;
import org.noear.solon.data.sqlink.core.api.crud.read.EmptyQuery;
import org.noear.solon.data.sqlink.core.api.crud.read.LQuery;
import org.noear.solon.data.sqlink.core.api.crud.update.LUpdate;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.transaction.Transaction;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;

import java.util.Collection;

public class Client
{
    private final IConfig config;

    public Client(IConfig config)
    {
        this.config = config;
    }

    public void useDs(String key)
    {
        config.getDataSourceManager().useDs(key);
    }

    public void useDefDs()
    {
        config.getDataSourceManager().useDefDs();
    }

    public Transaction beginTransaction(Integer isolationLevel)
    {
        return config.getTransactionManager().get(isolationLevel);
    }

    public Transaction beginTransaction()
    {
        return beginTransaction(null);
    }

    public <T> LQuery<T> query(@Recode Class<T> c)
    {
        return new LQuery<>(config, c);
    }

    public EmptyQuery queryEmptyTable()
    {
        return new EmptyQuery(config);
    }

    public <T> ObjectInsert<T> insert(@Recode T t)
    {
        ObjectInsert<T> objectInsert = new ObjectInsert<>(config, (Class<T>) t.getClass());
        return objectInsert.insert(t);
    }

    public <T> ObjectInsert<T> insert(@Recode Collection<T> ts)
    {
        ObjectInsert<T> objectInsert = new ObjectInsert<>(config, getType(ts));
        return objectInsert.insert(ts);
    }

    public <T> LUpdate<T> update(@Recode Class<T> c)
    {
        return new LUpdate<>(config, c);
    }

    public <T> LDelete<T> delete(@Recode Class<T> c)
    {
        return new LDelete<>(config, c);
    }

    private <T> Class<T> getType(Collection<T> ts)
    {
        for (T t : ts)
        {
            return (Class<T>) t.getClass();
        }
        throw new RuntimeException();
    }

    public IConfig getConfig()
    {
        return config;
    }
}
