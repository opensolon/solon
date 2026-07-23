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
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 延迟获取连接的连接代理
 *
 * <p>在首次创建 Statement（或 PreparedStatement、CallableStatement）之前，
 * 不会从目标数据源获取真实的物理连接。连接初始化属性（如 auto-commit、
 * 事务隔离级别、只读模式）会被暂存，等到真正获取连接时再一并应用。</p>
 *
 * <p>如果在整个事务过程中从未执行过 SQL，则 commit、rollback、close 调用
 * 会被静默忽略，不会占用连接池中的任何连接。</p>
 *
 * @author noear
 * @since 4.1
 * @see LazyConnectionDataSourceProxy
 */
public class LazyConnectionProxy implements Connection {
    private final DataSource targetDataSource;
    private final String username;
    private final String password;

    /**
     * 暂存的 autoCommit 设置，获取真实连接时应用
     */
    private Boolean autoCommit;

    /**
     * 暂存的 readOnly 设置，获取真实连接时应用
     */
    private Boolean readOnly;

    /**
     * 暂存的事务隔离级别，获取真实连接时应用
     */
    private Integer transactionIsolation;

    /**
     * 暂存的 catalog 设置，获取真实连接时应用
     */
    private String catalog;

    /**
     * 暂存的 schema 设置，获取真实连接时应用
     */
    private String schema;

    /**
     * 真实的物理连接（延迟获取）
     */
    private Connection target;

    /**
     * 代理连接是否已关闭
     */
    private boolean closed = false;

    public LazyConnectionProxy(DataSource targetDataSource) {
        this(targetDataSource, null, null);
    }

    public LazyConnectionProxy(DataSource targetDataSource, String username, String password) {
        this.targetDataSource = targetDataSource;
        this.username = username;
        this.password = password;
    }

    /**
     * 获取真实的物理连接，如果尚未获取则从数据源获取并应用暂存的设置
     */
    public Connection getTarget() throws SQLException {
        if (target == null) {
            if (closed) {
                throw new SQLException("Illegal operation: connection is closed");
            }
            // 从目标数据源获取物理连接
            if (username != null) {
                target = targetDataSource.getConnection(username, password);
            } else {
                target = targetDataSource.getConnection();
            }
            // 应用暂存的事务设置
            if (readOnly != null) {
                target.setReadOnly(readOnly);
            }
            if (transactionIsolation != null) {
                target.setTransactionIsolation(transactionIsolation);
            }
            if (autoCommit != null) {
                target.setAutoCommit(autoCommit);
            }
            if (catalog != null) {
                target.setCatalog(catalog);
            }
            if (schema != null) {
                target.setSchema(schema);
            }
        }
        return target;
    }

    /**
     * 是否已获取真实物理连接
     */
    public boolean hasTarget() {
        return target != null;
    }

    // ====== 事务相关方法：未获取连接时暂存，不触发连接获取 ======

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (hasTarget()) {
            target.setAutoCommit(autoCommit);
        } else {
            this.autoCommit = autoCommit;
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        if (hasTarget()) {
            return target.getAutoCommit();
        }
        return (this.autoCommit != null) ? this.autoCommit : true;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        if (hasTarget()) {
            target.setReadOnly(readOnly);
        } else {
            this.readOnly = readOnly;
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        if (hasTarget()) {
            return target.isReadOnly();
        }
        return (this.readOnly != null) ? this.readOnly : false;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        if (hasTarget()) {
            target.setTransactionIsolation(level);
        } else {
            this.transactionIsolation = level;
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        if (hasTarget()) {
            return target.getTransactionIsolation();
        }
        if (this.transactionIsolation != null) {
            return this.transactionIsolation;
        }
        return Connection.TRANSACTION_READ_COMMITTED;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        if (hasTarget()) {
            target.setCatalog(catalog);
        } else {
            this.catalog = catalog;
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        if (hasTarget()) {
            return target.getCatalog();
        }
        return this.catalog;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        if (hasTarget()) {
            target.setSchema(schema);
        } else {
            this.schema = schema;
        }
    }

    @Override
    public String getSchema() throws SQLException {
        if (hasTarget()) {
            return target.getSchema();
        }
        return this.schema;
    }

    // ====== 事务控制方法：未获取连接时静默忽略 ======

    @Override
    public void commit() throws SQLException {
        if (hasTarget()) {
            target.commit();
        }
        // 尚未获取真实连接，说明没有执行过 SQL，忽略 commit
    }

    @Override
    public void rollback() throws SQLException {
        if (hasTarget()) {
            target.rollback();
        }
        // 尚未获取真实连接，说明没有执行过 SQL，忽略 rollback
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        if (hasTarget()) {
            target.rollback(savepoint);
        }
    }

    // ====== 关闭与状态查询 ======

    @Override
    public void close() throws SQLException {
        if (closed) {
            return;
        }
        closed = true;
        if (hasTarget()) {
            target.close();
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        if (hasTarget()) {
            return target.isClosed();
        }
        return closed;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        if (hasTarget()) {
            return target.getWarnings();
        }
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        if (hasTarget()) {
            target.clearWarnings();
        }
    }

    // ====== 以下方法需要真实连接，触发延迟获取 ======

    @Override
    public Statement createStatement() throws SQLException {
        return getTarget().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getTarget().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return getTarget().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return getTarget().nativeSQL(sql);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getTarget().getMetaData();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getTarget().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getTarget().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getTarget().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getTarget().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getTarget().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        getTarget().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return getTarget().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return getTarget().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return getTarget().setSavepoint(name);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getTarget().releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getTarget().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getTarget().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getTarget().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getTarget().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getTarget().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getTarget().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return getTarget().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return getTarget().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return getTarget().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return getTarget().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        if (hasTarget()) {
            return target.isValid(timeout);
        }
        return true;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            getTarget().setClientInfo(name, value);
        } catch (SQLException e) {
            throw new SQLClientInfoException(e.getMessage(), null, e);
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            getTarget().setClientInfo(properties);
        } catch (SQLException e) {
            throw new SQLClientInfoException(e.getMessage(), null, e);
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return getTarget().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return getTarget().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getTarget().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getTarget().createStruct(typeName, attributes);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        if (hasTarget()) {
            target.abort(executor);
        }
        closed = true;
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        try {
            getTarget().setNetworkTimeout(executor, milliseconds);
        } catch (Throwable e) {
            //有些驱动不支持这个特性
        }
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        try {
            return getTarget().getNetworkTimeout();
        } catch (Throwable e) {
            //有些驱动不支持这个特性
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return getTarget().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return true;
        }
        return getTarget().isWrapperFor(iface);
    }

    @Override
    public String toString() {
        if (hasTarget()) {
            return "LazyConnectionProxy{target=" + target + "}";
        }
        return "LazyConnectionProxy{target=not-fetched-yet}";
    }
}