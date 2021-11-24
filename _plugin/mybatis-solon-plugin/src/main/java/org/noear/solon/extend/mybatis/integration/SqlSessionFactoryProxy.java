package org.noear.solon.extend.mybatis.integration;

import org.apache.ibatis.session.*;

import java.sql.Connection;

/**
 * @author noear
 * @since 1.6
 */
public class SqlSessionFactoryProxy implements SqlSessionFactory {
    SqlSessionFactory real;

    public SqlSessionFactoryProxy(SqlSessionFactory real) {
        this.real = real;
    }

    @Override
    public SqlSession openSession() {
        return new SqlSessionProxy(real.openSession());
    }

    @Override
    public SqlSession openSession(boolean b) {
        return new SqlSessionProxy(real.openSession(b));
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return new SqlSessionProxy(real.openSession(connection));
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel transactionIsolationLevel) {
        return new SqlSessionProxy(real.openSession(transactionIsolationLevel));
    }

    @Override
    public SqlSession openSession(ExecutorType executorType) {
        return new SqlSessionProxy(real.openSession(executorType));
    }

    @Override
    public SqlSession openSession(ExecutorType executorType, boolean b) {
        return new SqlSessionProxy(real.openSession(executorType, b));
    }

    @Override
    public SqlSession openSession(ExecutorType executorType, TransactionIsolationLevel transactionIsolationLevel) {
        return new SqlSessionProxy(real.openSession(executorType, transactionIsolationLevel));
    }

    @Override
    public SqlSession openSession(ExecutorType executorType, Connection connection) {
        return new SqlSessionProxy(real.openSession(executorType, connection));
    }

    @Override
    public Configuration getConfiguration() {
        return real.getConfiguration();
    }
}
