package org.springframework.jdbc.datasource;

import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceUtils {
    public static Connection getConnection(DataSource dataSource) throws SQLException {

        return TranUtils.getConnection(dataSource);
    }

    public static void releaseConnection(Connection conn, DataSource dataSource) {
        if (!TranUtils.inTrans()) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
