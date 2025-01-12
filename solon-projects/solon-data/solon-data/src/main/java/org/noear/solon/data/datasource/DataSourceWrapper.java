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
    private final DataSource real;

    public DataSource getReal() {
        return real;
    }

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
