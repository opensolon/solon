/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.data.datasource;

import org.noear.solon.Utils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
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
public abstract class AbstractRoutingDataSource implements RoutingDataSource, DataSource, Closeable {
    protected DataSource defaultTargetDataSource;
    protected Map<String, DataSource> targetDataSources;

    /**
     * 严格模式（启用后在未匹配到指定数据源时候会抛出异常,不启用则使用默认数据源.）
     */
    protected boolean strict;


    /**
     * 设置目标数据源集合（替换掉旧的）
     */
    public void setTargetDataSources(Map<String, DataSource> targetDataSources) {
        if (targetDataSources == null) { //null 就可以了，因为还有 defaultTargetDataSource
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        }

        this.targetDataSources = targetDataSources;
    }

    /**
     * 设置默认目标数据源
     */
    public void setDefaultTargetDataSource(DataSource defaultTargetDataSource) {
        if (defaultTargetDataSource == null) {
            throw new IllegalArgumentException("Property 'defaultTargetDataSource' is required");
        }

        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    /**
     * 设置严格模式
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }


    /**
     * 确定当前数据源键
     */
    public abstract String determineCurrentKey();


    /**
     * 确定当前目标数据源
     */
    public DataSource determineCurrentTarget() {
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

    @Override
    public void close() throws IOException {
        if (targetDataSources != null) {
            for (DataSource ds : targetDataSources.values()) {
                closeDataSource(ds);
            }
        }

        if (defaultTargetDataSource != null) {
            if (targetDataSources != null) {
                if (targetDataSources.containsValue(defaultTargetDataSource)) {
                    return;
                }
            }

            closeDataSource(defaultTargetDataSource);
        }
    }

    /**
     * 尝试关闭数据源
     */
    protected void closeDataSource(DataSource ds) throws IOException {
        if (ds instanceof Closeable) {
            ((Closeable) ds).close();
        }
    }
}
