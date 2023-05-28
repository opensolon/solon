package org.noear.solon.data.shardingds;

import org.apache.shardingsphere.solon.ShardingSphereSupplier;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 分片数据源
 *
 * @author noear
 * @since 2.2
 */
public class ShardingDataSource implements DataSource {
    final DataSource real;

    /**
     * 获取真实数据源
     * */
    public DataSource getReal() {
        return real;
    }

    public ShardingDataSource(Properties props) {
        real = new ShardingSphereSupplier(props).get();
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
}
