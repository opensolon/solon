package org.noear.solon.extend.data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TranUtils {

    public static Connection getConnection(DataSource ds) throws SQLException {
        return ds.getConnection();
    }
}
