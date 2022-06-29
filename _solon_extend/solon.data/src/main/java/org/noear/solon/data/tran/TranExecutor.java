package org.noear.solon.data.tran;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.ext.RunnableEx;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务执行器
 *
 * @author noear
 * @since 1.0
 * */
public interface TranExecutor {
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
     *
     * @param ds 数据源
     */
    default Connection getConnection(DataSource ds) throws SQLException {
        return ds.getConnection();
    }

    /**
     * 执行
     * */
    default void execute(Tran meta, RunnableEx runnable) throws Throwable{

    }
}
