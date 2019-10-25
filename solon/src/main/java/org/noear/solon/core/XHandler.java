package org.noear.solon.core;

/**
 * 通用处理接口
 * */
public interface XHandler {
    void handle(XContext context) throws Throwable;
}
