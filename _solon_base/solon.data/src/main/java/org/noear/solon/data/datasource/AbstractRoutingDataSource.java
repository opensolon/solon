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
 * @since 1.11
 */
public abstract class AbstractRoutingDataSource implements DataSource {
    private DataSource defaultTargetDataSource;
    private Map<String, DataSource> targetDataSources;

    /**
     * 严格模式（启用后在未匹配到指定数据源时候会抛出异常,不启用则使用默认数据源.）
     * */
    private boolean strict;

    public void setTargetDataSources(Map<String, DataSource> targetDataSources) {
        this.targetDataSources = targetDataSources;
    }

    public void setDefaultTargetDataSource(DataSource defaultTargetDataSource) {
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * 检查属性设置
     * */
    public void checkPropertiesSet(){
        if (targetDataSources == null || targetDataSources.size() == 0) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        }

        if (defaultTargetDataSource == null) {
            throw new IllegalArgumentException("Property 'defaultTargetDataSource' is required");
        }
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
                if (strict) {
                    throw new IllegalStateException("Cannot determine target DataSource for key [" + targetKey + "]");
                } else {
                    tmp = defaultTargetDataSource;
                }
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
