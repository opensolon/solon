package net.hasor.db.solon.integration;

import net.hasor.cobble.function.EFunction;
import net.hasor.db.transaction.DataSourceManager;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author noear
 * @see 1.6
 */
public class AccessorApplyImpl implements EFunction<DataSource, Connection, SQLException> {
    private static EFunction<DataSource, Connection, SQLException> instance = new AccessorApplyImpl();

    public static EFunction<DataSource, Connection, SQLException> getInstance() {
        return instance;
    }

    @Override
    public Connection eApply(DataSource dataSource) throws SQLException {
        if (TranUtils.inTrans()) {
            return TranUtils.getConnection(dataSource);
        } else {
            return DataSourceManager.getConnection(dataSource);
        }
    }
}
