package org.noear.solon.core;

public interface TranSession {
    void open();
    void commit();
    void rollback();
    void close();


    /**
     * 挂起
     * */
    void hangup();
    /**
     * 恢复
     * */
    void restore();
}
