package org.noear.solon.data.sqlink.base.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultTransaction implements Transaction
{
    protected Connection connection;
    protected final DataSource dataSource;
    protected final Integer isolationLevel;
    protected final TransactionManager manager;

    public DefaultTransaction(Integer isolationLevel, DataSource dataSource, TransactionManager manager)
    {
        this.isolationLevel = isolationLevel;
        this.dataSource = dataSource;
        this.manager = manager;
    }

    public Integer getIsolationLevel()
    {
        return isolationLevel;
    }

    public void commit()
    {
        try
        {
            connection.commit();
            close();
        }
        catch (SQLException e)
        {
            rollback();
            throw new RuntimeException(e);
        }
        finally
        {
            clear();
        }
    }

    public void rollback()
    {
        try
        {
            connection.rollback();
            close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            clear();
        }
    }

    @Override
    public void close()
    {
        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            clear();
        }
    }

    protected void clear()
    {
        manager.remove();
    }

    public Connection getConnection() throws SQLException
    {
        if (connection == null)
        {
            connection = dataSource.getConnection();
        }
        connection.setAutoCommit(false);
        if (isolationLevel != null) connection.setTransactionIsolation(isolationLevel);
        return connection;
    }
}
