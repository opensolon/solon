package org.noear.solon.core;

import java.sql.SQLException;

/**
 * 事务会话
 * */
public interface TranSession {
    /**
     * 开始
     * */
    void start(TranIsolation isolation) throws SQLException;
    /**
     * 提交
     * */
    void commit() throws SQLException;
    /**
     * 回滚
     * */
    void rollback() throws SQLException;
    /**
     * 结束
     * */
    void end();

    /**
     * 关闭
     * */
    void close() throws SQLException;


    /**
     * 挂起
     * */
    void suspend();
    /**
     * 恢复
     * */
    void resume();
}
