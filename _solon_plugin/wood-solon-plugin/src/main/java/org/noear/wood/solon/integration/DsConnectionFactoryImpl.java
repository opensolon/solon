package org.noear.wood.solon.integration;

import org.noear.solon.data.tran.TranUtils;
import org.noear.wood.DbConnectionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author noear
 * @since 1.10
 */
class DsConnectionFactoryImpl extends DbConnectionFactory {
    @Override
    public Connection getConnection(DataSource ds) throws SQLException {
        return TranUtils.getConnection(ds);
    }
}
