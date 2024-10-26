package org.noear.solon.data.sqlink.base.session;


import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public class DefaultSqlSession implements SqlSession
{
    protected final DataSourceManager dataSourceManager;
    protected final TransactionManager transactionManager;

    public DefaultSqlSession(DataSourceManager dataSourceManager, TransactionManager transactionManager)
    {
        this.dataSourceManager = dataSourceManager;
        this.transactionManager = transactionManager;
    }

    public <R> R executeQuery(Function<ResultSet, R> func, String sql, Collection<Object> values)
    {
        if (!transactionManager.currentThreadInTransaction())
        {
            try (Connection connection = dataSourceManager.getConnection())
            {
                return executeQuery(connection, func, sql, values);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            try
            {
                Connection connection;
                if (transactionManager.isOpenTransaction())
                {
                    connection = transactionManager.getCurTransaction().getConnection();
                }
                else
                {
                    connection = dataSourceManager.getConnection();
                }
                return executeQuery(connection, func, sql, values);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private <R> R executeQuery(Connection connection, Function<ResultSet, R> func, String sql, Collection<Object> values)
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            setObjects(preparedStatement, values);
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                return func.invoke(resultSet);
            }
        }
        catch (SQLException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public long executeUpdate(String sql, List<SqlValue> values)
    {
        if (!transactionManager.currentThreadInTransaction())
        {
            try (Connection connection = dataSourceManager.getConnection())
            {
                return executeUpdate(connection, sql, values);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            try
            {
                Connection connection;
                if (transactionManager.isOpenTransaction())
                {
                    connection = transactionManager.getCurTransaction().getConnection();
                }
                else
                {
                    connection = dataSourceManager.getConnection();
                }
                return executeUpdate(connection, sql, values);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public long executeUpdate(String sql, List<Object> values, Object... o)
    {
        if (!transactionManager.currentThreadInTransaction())
        {
            try (Connection connection = dataSourceManager.getConnection())
            {
                return executeUpdate(connection, sql, values);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            try
            {
                Connection connection;
                if (transactionManager.isOpenTransaction())
                {
                    connection = transactionManager.getCurTransaction().getConnection();
                }
                else
                {
                    connection = dataSourceManager.getConnection();
                }
                return executeUpdate(connection, sql, values);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public long batchExecuteUpdate(String sql, long limit, List<SqlValue> values)
    {
        if (!transactionManager.currentThreadInTransaction())
        {
            try (Connection connection = dataSourceManager.getConnection())
            {
                return batchExecuteUpdate(connection, sql, limit, values);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            try
            {
                Connection connection;
                if (transactionManager.isOpenTransaction())
                {
                    connection = transactionManager.getCurTransaction().getConnection();
                }
                else
                {
                    connection = dataSourceManager.getConnection();
                }
                return batchExecuteUpdate(connection, sql, limit, values);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    protected long executeUpdate(Connection connection, String sql, List<SqlValue> values)
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            setObjectsIfNull(preparedStatement, values);
            return preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected long executeUpdate(Connection connection, String sql, Collection<Object> values, Object... o)
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            setObjects(preparedStatement, values);
            return preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected long batchExecuteUpdate(Connection connection, String sql, long limit, List<SqlValue> values)
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            batchSetObjects(preparedStatement, limit, values);
            return preparedStatement.executeBatch().length;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected void setObjects(PreparedStatement preparedStatement, Collection<Object> values) throws SQLException
    {
        int index = 1;
        for (Object value : values)
        {
            if (value instanceof Enum)
            {
                preparedStatement.setString(index++, value.toString());
            }
            else if (value instanceof Character)
            {
                preparedStatement.setString(index++, value.toString());
            }
            else if (value instanceof Integer)
            {
                preparedStatement.setInt(index++, (int) value);
            }
            else if (value instanceof String)
            {
                preparedStatement.setString(index++, value.toString());
            }
            else
            {
                preparedStatement.setObject(index++, value);
            }
        }
    }

    protected void setObjectsIfNull(PreparedStatement preparedStatement, List<SqlValue> values) throws SQLException
    {
        for (int i = 1; i <= values.size(); i++)
        {
            SqlValue value = values.get(i - 1);
            Object o = value.getValues().get(0);
            if (o == null)
            {
                preparedStatement.setNull(i, convert(value.getType()));
            }
            else
            {
                preparedStatement.setObject(i, o);
            }
        }
    }

    protected void batchSetObjects(PreparedStatement preparedStatement, long limit, List<SqlValue> values) throws SQLException
    {
        for (long i = 0; i < limit; i++)
        {
            int index = 0;
            for (SqlValue value : values)
            {
                Object o = value.getValues().get((int) i);
                if (o == null)
                {
                    preparedStatement.setNull(++index, convert(value.getType()));
                }
                else
                {
                    preparedStatement.setObject(++index, o);
                }
            }
            preparedStatement.addBatch();
        }
    }

    protected static int convert(Class<?> type)
    {
        if (type == String.class)
        {
            return Types.VARCHAR;
        }
        else if (type == Integer.class || type == int.class)
        {
            return Types.INTEGER;
        }
        else if (type == Long.class || type == long.class)
        {
            return Types.BIGINT;
        }
        else if (type == Double.class || type == double.class)
        {
            return Types.DOUBLE;
        }
        else if (type == Float.class || type == float.class)
        {
            return Types.FLOAT;
        }
        else if (type == Boolean.class || type == boolean.class)
        {
            return Types.BOOLEAN;
        }
        else if (type == Date.class || type == LocalDate.class)
        {
            return Types.DATE;
        }
        else if (type == Time.class || type == LocalTime.class)
        {
            return Types.TIME;
        }
        else if (type == Timestamp.class || type == LocalDateTime.class)
        {
            return Types.TIMESTAMP;
        }
        else if (type == java.math.BigDecimal.class)
        {
            return Types.DECIMAL;
        }
        else
        {
            return Types.OTHER; // Default to OTHER type if not recognized
        }
    }
}
