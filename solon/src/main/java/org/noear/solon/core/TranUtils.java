package org.noear.solon.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TranUtils {
    /**
     * 是否在事务中
     */
    public static boolean inTrans() {
        return XBridge.tranExecutor().inTrans();
    }

    /**
     * 是否在事务中且只读
     */
    public static boolean inTransAndReadOnly() {
        return XBridge.tranExecutor().inTransAndReadOnly();
    }

    /**
     * 获取链接
     */
    public static Connection getConnection(DataSource ds) throws SQLException {
        return XBridge.tranExecutor().getConnection(ds);
    }
}
