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
import org.noear.solon.data.sqlink.base.annotation.OnInsertDefaultValue;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.generate.DynamicGenerator;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.cast;

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

    public <R> R executeQuery(Function<ResultSet, R> func, String sql, Collection<Object> values) {
        if (!transactionManager.currentThreadInTransaction()) {
            try (Connection connection = dataSourceManager.getConnection()) {
                return executeQuery(connection, func, sql, values);
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
                return executeQuery(connection, func, sql, values);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <R> R executeQuery(Connection connection, Function<ResultSet, R> func, String sql, Collection<Object> values) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setObjects(preparedStatement, values);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return func.invoke(resultSet);
            }
        }
        catch (SQLException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long executeInsert(String sql, List<Object> values) {
        if (!transactionManager.currentThreadInTransaction()) {
            try (Connection connection = dataSourceManager.getConnection()) {
                return executeInsert(connection, sql, values);
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
                return executeInsert(connection, sql, values);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public long executeDelete(String sql, List<Object> values) {
        return executeUpdate(sql, values);
    }

    public long executeUpdate(String sql, List<Object> values) {
        if (!transactionManager.currentThreadInTransaction()) {
            try (Connection connection = dataSourceManager.getConnection()) {
                return executeUpdate(connection, sql, values);
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
                return executeUpdate(connection, sql, values);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected long executeInsert(Connection connection, String sql, Collection<Object> values) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setObjectsByEntity(preparedStatement, values);
            if (values.size() == 1) {
                return preparedStatement.executeUpdate();
            }
            else {
                return preparedStatement.executeBatch().length;
            }
        }
        catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected long executeUpdate(Connection connection, String sql, Collection<Object> values) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setObjects(preparedStatement, values);
            return preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setObjects(PreparedStatement preparedStatement, Collection<Object> values) throws SQLException {
        int index = 1;
        for (Object value : values) {
            if (value instanceof SqlValue) {
                SqlValue sqlValue = (SqlValue) value;
                sqlValue.preparedStatementSetValue(preparedStatement, index++);
            }
            else {
                TypeHandlerManager.get(value.getClass()).setValue(preparedStatement, index++, value);
            }
        }
    }

//    protected void setObjectsIfNull(PreparedStatement preparedStatement, List<SqlValue> values) throws SQLException
//    {
//        for (int i = 1; i <= values.size(); i++)
//        {
//            SqlValue value = values.get(i - 1);
//            Object o = value.getValues().get(0);
//            if (o == null)
//            {
//                preparedStatement.setNull(i, convert(value.getType()));
//            }
//            else
//            {
//                preparedStatement.setObject(i, o);
//            }
//        }
//    }

//    protected static int convert(Class<?> type)
//    {
//        if (type == String.class)
//        {
//            return Types.VARCHAR;
//        }
//        else if (type == Integer.class || type == int.class)
//        {
//            return Types.INTEGER;
//        }
//        else if (type == Long.class || type == long.class)
//        {
//            return Types.BIGINT;
//        }
//        else if (type == Double.class || type == double.class)
//        {
//            return Types.DOUBLE;
//        }
//        else if (type == Float.class || type == float.class)
//        {
//            return Types.FLOAT;
//        }
//        else if (type == Boolean.class || type == boolean.class)
//        {
//            return Types.BOOLEAN;
//        }
//        else if (type == Date.class || type == LocalDate.class)
//        {
//            return Types.DATE;
//        }
//        else if (type == Time.class || type == LocalTime.class)
//        {
//            return Types.TIME;
//        }
//        else if (type == Timestamp.class || type == LocalDateTime.class)
//        {
//            return Types.TIMESTAMP;
//        }
//        else if (type == java.math.BigDecimal.class)
//        {
//            return Types.DECIMAL;
//        }
//        else
//        {
//            return Types.OTHER; // Default to OTHER type if not recognized
//        }
//    }

    protected void setObjectsByEntity(PreparedStatement preparedStatement, Collection<Object> values) throws SQLException, InvocationTargetException, IllegalAccessException {
        boolean batch = values.size() > 1;
        for (Object value : values) {
            int index = 1;
            MetaData metaData = MetaDataCache.getMetaData(value.getClass());
            for (FieldMetaData fieldMetaData : metaData.getNotIgnorePropertys()) {
                ITypeHandler<?> typeHandler = fieldMetaData.getTypeHandler();
                Object fieldValue = fieldMetaData.getGetter().invoke(value);
                OnInsertDefaultValue onInsert = fieldMetaData.getOnInsertDefaultValues();
                if (onInsert != null) {
                    switch (onInsert.strategy()) {
                        case DataBase:
                            // 交给数据库
                            break;
                        case Static:
                            if (fieldValue == null) {
                                typeHandler.setStringValue(preparedStatement, index++, onInsert.value());
                            }
                            else {
                                typeHandler.setValue(preparedStatement, index++, cast(fieldValue));
                            }
                            break;
                        case Dynamic:
                            if (fieldValue == null) {
                                fieldValue = DynamicGenerator.get(onInsert.dynamic()).generate(config, fieldMetaData);
                            }
                            typeHandler.setValue(preparedStatement, index++, cast(fieldValue));
                            break;
                    }
                }
                else {
                    typeHandler.setValue(preparedStatement, index++, cast(fieldValue));
                }
            }
            if (batch) {
                preparedStatement.addBatch();
            }
        }
    }
}
