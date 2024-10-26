package org.noear.solon.data.sqlink.plugin.datasource;



import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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
