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
package org.noear.solon.data.sqlink.plugin.datasource;

import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SolonDataSourceManagerWrap implements DataSourceManager
{
    private DataSourceManager dataSourceManager;

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSourceManager.getConnection();
    }

    @Override
    public DataSource getDataSource()
    {
        return dataSourceManager.getDataSource();
    }

    @Override
    public void useDs(String key)
    {
        dataSourceManager.useDs(key);
    }

    @Override
    public void useDefDs()
    {
        dataSourceManager.useDefDs();
    }

    @Override
    public String getDsKey()
    {
        return dataSourceManager.getDsKey();
    }

    public void setDataSourceManager(DataSourceManager dataSourceManager)
    {
        if (hasDataSource())
        {
            throw new RuntimeException("DataSourceManager not null");
        }
        this.dataSourceManager = dataSourceManager;
    }

    public boolean hasDataSource()
    {
        return dataSourceManager != null;
    }
}
