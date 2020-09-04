package org.noear.solon.core;

import org.noear.solon.annotation.XNote;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务工具
 * */
public class XTranUtils {
    /**
     * 是否在事务中
     */
    @XNote("是否在事务中")
    public static boolean inTrans() {
        return XBridge.tranExecutor().inTrans();
    }

    /**
     * 是否在事务中且只读
     */
    @XNote("是否在事务中且只读")
    public static boolean inTransAndReadOnly() {
        return XBridge.tranExecutor().inTransAndReadOnly();
    }

    /**
     * 获取链接
     */
    @XNote("获取链接")
    public static Connection getConnection(DataSource ds) throws SQLException {
        return XBridge.tranExecutor().getConnection(ds);
    }
}
