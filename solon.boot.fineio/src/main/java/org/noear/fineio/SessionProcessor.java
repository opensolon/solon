package org.noear.fineio;


/**
 * 会话处理
 * */
public interface SessionProcessor<T> {
    void process(NetSession<T> session);
}
