package org.noear.solon.data.datasource;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 数据源包装器
 *
 * @author noear
 * @since 2.7
 */
public class DataSourceWrapper implements DataSource , Closeable {
    protected final DataSource real;

    public DataSourceWrapper(DataSource real) {
        this.real = real;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return real.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return real.getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return real.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return real.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return real.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        real.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        real.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return real.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return real.getParentLogger();
    }

    @Override
    public void close() throws IOException {
        if (real instanceof Closeable) {
            ((Closeable) real).close();
        }
    }
}
