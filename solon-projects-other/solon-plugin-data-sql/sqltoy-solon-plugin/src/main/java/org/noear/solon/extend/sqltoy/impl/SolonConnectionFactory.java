package org.noear.solon.extend.sqltoy.impl;

import org.noear.solon.data.tran.TranUtils;
import org.sagacity.sqltoy.integration.ConnectionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SolonConnectionFactory implements ConnectionFactory {
    @Override
    public Connection getConnection(DataSource dataSource) {
        try {
            return TranUtils.getConnection(dataSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseConnection(Connection connection, DataSource dataSource) {
        if (!TranUtils.inTrans()) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
