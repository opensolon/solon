package org.noear.solon.core;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务执行器
 * */
public interface TranExecutor {
    /**
     * 是否在事务中
     */
    default boolean inTrans() {
        return false;
    }

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

    /**
     * 执行事务
     */
    void execute(XTran anno, RunnableEx runnable) throws Throwable;
}
