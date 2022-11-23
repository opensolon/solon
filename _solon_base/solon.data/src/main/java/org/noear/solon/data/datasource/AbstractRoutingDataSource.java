package org.noear.solon.data.datasource;

import org.noear.solon.Utils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 可路由数据源
 *
 * @author noear
 * @since 1.10
 */
public abstract class AbstractRoutingDataSource implements DataSource {
    protected DataSource defaultTargetDataSource;
    protected Map<String, DataSource> targetDataSources;

    /**
     * 初始化
     * */
    protected void initDo(DataSource defaultTargetDataSource, Map<String, DataSource> targetDataSources) {
        this.targetDataSources = targetDataSources;
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    /**
     * 确定当前数据源键
     */
    protected abstract String determineCurrentKey();


    /**
     * 确定当前目标数据源
     */
    protected DataSource determineCurrentTarget() {
        String targetKey = determineCurrentKey();

        if (Utils.isEmpty(targetKey)) {
            return defaultTargetDataSource;
        } else {
            DataSource tmp = targetDataSources.get(targetKey);

            if (tmp == null) {
                throw new IllegalStateException("Cannot determine target DataSource for key [" + targetKey + "]");
            }

            return tmp;
        }
    }

    //////////////////////////

    @Override
    public Connection getConnection() throws SQLException {
        return determineCurrentTarget().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineCurrentTarget().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return iface.isInstance(this) ? (T) this : determineCurrentTarget().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || determineCurrentTarget().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return determineCurrentTarget().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        determineCurrentTarget().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        determineCurrentTarget().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return determineCurrentTarget().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return determineCurrentTarget().getParentLogger();
    }
}
