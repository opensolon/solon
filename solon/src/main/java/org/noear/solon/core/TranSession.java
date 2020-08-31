package org.noear.solon.core;

public interface TranSession {
    void open();
    void commit();
    void rollback();
    void close();
}
