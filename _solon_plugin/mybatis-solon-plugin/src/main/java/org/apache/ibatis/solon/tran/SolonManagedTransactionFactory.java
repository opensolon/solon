package org.apache.ibatis.solon.tran;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author noear
 * @since 1.6
 */
public class SolonManagedTransactionFactory implements TransactionFactory {
    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public Transaction newTransaction(Connection connection) {
        throw new UnsupportedOperationException("New Solon transactions require a DataSource");
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel transactionIsolationLevel, boolean b) {
        return new SolonManagedTransaction(dataSource);
    }
}
