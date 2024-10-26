package org.noear.solon.data.sqlink.base.session;


import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;

public class DefaultSqlSessionFactory implements SqlSessionFactory
{
    protected final DataSourceManager dataSourceManager;
    protected final TransactionManager transactionManager;

    public DefaultSqlSessionFactory(DataSourceManager dataSourceManager, TransactionManager transactionManager)
    {
        this.dataSourceManager = dataSourceManager;
        this.transactionManager = transactionManager;
    }

    @Override
    public SqlSession getSession()
    {
        return new DefaultSqlSession(dataSourceManager, transactionManager);
    }
}
