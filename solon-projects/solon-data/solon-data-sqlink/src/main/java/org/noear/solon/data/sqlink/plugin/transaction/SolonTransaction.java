package org.noear.solon.data.sqlink.plugin.transaction;

import org.noear.solon.data.sqlink.base.transaction.DefaultTransaction;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SolonTransaction extends DefaultTransaction
{
    public SolonTransaction(Integer isolationLevel, DataSource dataSource, TransactionManager manager)
    {
        super(isolationLevel,dataSource,manager);
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        if (connection == null)
        {
            connection = TranUtils.getConnection(dataSource);
        }
        connection.setAutoCommit(false);
        if (isolationLevel != null) connection.setTransactionIsolation(isolationLevel);
        return connection;
    }

}
