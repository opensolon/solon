package net.hasor.dbvisitor.solon.integration;

import net.hasor.dbvisitor.jdbc.DynamicConnection;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author noear
 * @see 1.8
 */
public class DynamicConnectionImpl implements DynamicConnection {
    private DataSource dataSource;

    public DynamicConnectionImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return TranUtils.getConnection(dataSource);
    }

    @Override
    public void releaseConnection(Connection conn) throws SQLException {
        if (TranUtils.inTrans() == false) {
            conn.close();
        }
    }
}
