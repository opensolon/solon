package org.noear.solon.extend.activerecord.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.noear.solon.data.tran.TranUtils;

/**
 * 数据源代理（实现与Solon事务对接）
 *
 * @author noear
 * @since 1.4
 */
public class DataSourceProxyImpl implements DataSource , Closeable {
    private DataSource ds;

    public DataSourceProxyImpl(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (TranUtils.inTrans()) {
            return TranUtils.getConnection(this.ds);
        } else {
            return this.ds.getConnection();
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.ds.getConnection(username, password);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.ds.getLoginTimeout();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.ds.getLogWriter();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.ds.getParentLogger();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.ds.isWrapperFor(iface);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.ds.setLoginTimeout(seconds);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.ds.setLogWriter(out);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.ds.unwrap(iface);
    }

    @Override
    public void close() throws IOException {
        if (ds instanceof Closeable) {
            ((Closeable) ds).close();
        }
    }
}