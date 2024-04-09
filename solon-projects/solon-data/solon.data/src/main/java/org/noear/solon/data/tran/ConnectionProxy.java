package org.noear.solon.data.tran;

import org.noear.solon.data.datasource.ConnectionWrapper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 连接代理（用于事务控制）
 *
 * @author noear
 * @since 2.7
 */
public class ConnectionProxy extends ConnectionWrapper {
    private final boolean inTrans;

    public ConnectionProxy(Connection real) {
        super(real);
        inTrans = TranUtils.inTrans();
    }

    @Override
    public void commit() throws SQLException {
        if (inTrans == false && super.getAutoCommit() == false) {
            super.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (inTrans == false && super.getAutoCommit() == false) {
            super.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (inTrans == false) {
            super.close();
        }
    }
}