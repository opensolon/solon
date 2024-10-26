package org.noear.solon.data.sqlink.base.dataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceManager
{
    Connection getConnection() throws SQLException;

    DataSource getDataSource();

    void useDs(String key);

    void useDefDs();

    String getDsKey();
}