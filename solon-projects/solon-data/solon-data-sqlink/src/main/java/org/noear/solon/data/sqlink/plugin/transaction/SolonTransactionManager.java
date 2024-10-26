package org.noear.solon.data.sqlink.plugin.transaction;

import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.transaction.DefaultTransactionManager;
import org.noear.solon.data.sqlink.base.transaction.Transaction;
import org.noear.solon.data.tran.TranUtils;

public class SolonTransactionManager extends DefaultTransactionManager
{
    public SolonTransactionManager(DataSourceManager dataSourceManager)
    {
        super(dataSourceManager);
    }

    @Override
    public Transaction get(Integer isolationLevel)
    {
        if (currentThreadInTransaction())
        {
            throw new RuntimeException("不支持多重事务");
        }
        SolonTransaction solonTransaction = new SolonTransaction(isolationLevel, dataSourceManager.getDataSource(), this);
        curTransaction.set(solonTransaction);
        return solonTransaction;
    }

    @Override
    public boolean currentThreadInTransaction()
    {
        return TranUtils.inTrans() || isOpenTransaction();
    }
}
