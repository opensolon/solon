package org.noear.solon.core;

/**
 * 通用处理接口接收槽
 * */
public interface XHandlerSlots {
    void before(String expr, XMethod method, int index, XHandler handler);
    void after(String expr, XMethod method, int index, XHandler handler);
    void add(String expr, XMethod method, XHandler handler);
}
