package org.noear.solon.data.tran;

import org.noear.solon.data.datasource.DataSourceWrapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源代理（用于事务控制）
 *
 * @author noear
 * @since 2.7
 */
public class DataSourceProxy extends DataSourceWrapper {
    public DataSourceProxy(DataSource real) {
        super(real);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return TranUtils.getConnectionProxy(this.real);
    }
}
