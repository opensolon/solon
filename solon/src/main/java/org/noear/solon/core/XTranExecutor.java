package org.noear.solon.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务执行器
 * */
public interface XTranExecutor {
    /**
     * 是否在事务中
     */
    boolean inTrans();

    /**
     * 是否在事务中且只读
     */
    default boolean inTransAndReadOnly() {
        return false;
    }

    /**
     * 获取链接
     */
    default Connection getConnection(DataSource ds) throws SQLException {
        return ds.getConnection();
    }
}
