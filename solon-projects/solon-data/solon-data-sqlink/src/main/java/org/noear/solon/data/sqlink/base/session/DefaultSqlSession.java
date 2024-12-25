/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.data.sqlink.base.session;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class DefaultSqlSession implements SqlSession {
    protected final SqLinkConfig config;
    protected final DataSourceManager dataSourceManager;
    protected final TransactionManager transactionManager;

    public DefaultSqlSession(SqLinkConfig config, DataSourceManager dataSourceManager, TransactionManager transactionManager) {
        this.config = config;
        this.dataSourceManager = dataSourceManager;
        this.transactionManager = transactionManager;
    }

    public <R> R executeQuery(Function<ResultSet, R> func, String sql, Collection<SqlValue> sqlValues) {
        if (!transactionManager.currentThreadInTransaction()) {
            try (Connection connection = dataSourceManager.getConnection()) {
                return executeQuery(connection, func, sql, sqlValues);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                Connection connection;
                if (transactionManager.isOpenTransaction()) {
                    connection = transactionManager.getCurTransaction().getConnection();
                }
                else {
                    connection = dataSourceManager.getConnection();
                }
                return executeQuery(connection, func, sql, sqlValues);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <R> R executeQuery(Connection connection, Function<ResultSet, R> func, String sql, Collection<SqlValue> sqlValues) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setObjects(preparedStatement, sqlValues);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return func.invoke(resultSet);
            }
        }
        catch (SQLException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long executeInsert(String sql, Collection<SqlValue> sqlValues, int length) {
        if (!transactionManager.currentThreadInTransaction()) {
            try (Connection connection = dataSourceManager.getConnection()) {
                boolean autoCommit = connection.getAutoCommit();
                connection.setAutoCommit(true);
                long count = executeInsert(connection, sql, sqlValues, length);
                connection.setAutoCommit(autoCommit);
                return count;
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                Connection connection;
                if (transactionManager.isOpenTransaction()) {
                    connection = transactionManager.getCurTransaction().getConnection();
                }
                else {
                    connection = dataSourceManager.getConnection();
                }
                return executeInsert(connection, sql, sqlValues, length);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public long executeDelete(String sql, Collection<SqlValue> sqlValues) {
        return executeUpdate(sql, sqlValues);
    }

    public long executeUpdate(String sql, Collection<SqlValue> sqlValues) {
        if (!transactionManager.currentThreadInTransaction()) {
            try (Connection connection = dataSourceManager.getConnection()) {
                boolean autoCommit = connection.getAutoCommit();
                connection.setAutoCommit(true);
                long count = executeUpdate(connection, sql, sqlValues);
                connection.setAutoCommit(autoCommit);
                return count;
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                Connection connection;
                if (transactionManager.isOpenTransaction()) {
                    connection = transactionManager.getCurTransaction().getConnection();
                }
                else {
                    connection = dataSourceManager.getConnection();
                }
                return executeUpdate(connection, sql, sqlValues);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected long executeInsert(Connection connection, String sql, Collection<SqlValue> sqlValues, int length) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            boolean batch = setObjects(preparedStatement, sqlValues, length);
            if (batch) {
                return preparedStatement.executeBatch().length;
            }
            else {
                return preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected long executeUpdate(Connection connection, String sql, Collection<SqlValue> sqlValues) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setObjects(preparedStatement, sqlValues);
            return preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setObjects(PreparedStatement preparedStatement, Collection<SqlValue> sqlValues) throws SQLException {
        int index = 1;
        for (SqlValue sqlValue : sqlValues) {
            sqlValue.preparedStatementSetValue(config, preparedStatement, index++);
        }
    }

    protected boolean setObjects(PreparedStatement preparedStatement, Collection<SqlValue> sqlValues, int length) throws SQLException, InvocationTargetException, IllegalAccessException {
        int size = sqlValues.size();
        boolean batch = size > length;
        int index = 1;
        for (SqlValue sqlValue : sqlValues) {
            sqlValue.preparedStatementSetValue(config, preparedStatement, index++);
            if (index > length && batch) {
                index = 1;
                preparedStatement.addBatch();
            }
        }
        return batch;
    }
}
