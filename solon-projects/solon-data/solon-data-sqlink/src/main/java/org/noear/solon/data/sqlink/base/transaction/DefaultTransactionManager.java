package org.noear.solon.data.sqlink.base.transaction;

import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;

public class DefaultTransactionManager implements TransactionManager
{
    protected final DataSourceManager dataSourceManager;
    protected final ThreadLocal<Transaction> curTransaction = new ThreadLocal<>();

    public DefaultTransactionManager(DataSourceManager dataSourceManager)
    {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public Transaction get(Integer isolationLevel)
    {
        if (currentThreadInTransaction())
        {
            throw new RuntimeException("不支持多重事务");
        }
        DefaultTransaction defaultTransaction = new DefaultTransaction(isolationLevel, dataSourceManager.getDataSource(), this);
        curTransaction.set(defaultTransaction);
        return defaultTransaction;
    }

    @Override
    public void remove()
    {
        curTransaction.remove();
    }

    @Override
    public Transaction getCurTransaction()
    {
        return curTransaction.get();
    }

    @Override
    public boolean currentThreadInTransaction()
    {
        return isOpenTransaction();
    }

    @Override
    public boolean isOpenTransaction()
    {
        return curTransaction.get() != null;
    }
}
