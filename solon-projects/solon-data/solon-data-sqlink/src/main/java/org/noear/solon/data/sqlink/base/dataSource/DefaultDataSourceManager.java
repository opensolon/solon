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
package org.noear.solon.data.sqlink.base.dataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDataSourceManager implements DataSourceManager
{
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    private final ThreadLocal<String> dsKey = new ThreadLocal<>();

    private final static String Default = "default";

    public DefaultDataSourceManager(DataSource defluteDataSource)
    {
        dataSourceMap.put(Default, defluteDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return getDataSource().getConnection();
    }

    public DataSource getDataSource()
    {
        String key = getDsKey();
        if (key == null)
        {
            return getDataSource(Default);
        }
        return getDataSource(key);
    }

    private DataSource getDataSource(String key)
    {
        DataSource dataSource = dataSourceMap.get(key);
        if (dataSource == null)
        {
            throw new RuntimeException("No DataSource found for key: " + key);
        }
        return dataSource;
    }

    public void useDs(String key)
    {
        dsKey.set(key);
    }

    @Override
    public void useDefDs()
    {
        dsKey.remove();
    }

    public String getDsKey()
    {
        return dsKey.get();
    }
}
