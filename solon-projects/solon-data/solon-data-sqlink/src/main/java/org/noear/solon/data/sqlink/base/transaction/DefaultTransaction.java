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
package org.noear.solon.data.sqlink.base.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author kiryu1223
 * @since 3.0
 */
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
