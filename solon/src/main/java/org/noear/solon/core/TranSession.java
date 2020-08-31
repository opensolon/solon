package org.noear.solon.core;

import java.sql.SQLException;

public interface TranSession {
    void start() throws SQLException;
    void commit() throws SQLException;
    void rollback() throws SQLException;
    void end();
    void close() throws SQLException;


    /**
     * 挂起
     * */
    void hangup();
    /**
     * 恢复
     * */
    void restore();
}
