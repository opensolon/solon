package org.noear.solon.core;

/**
 * 通用处理接口（实现：XContext + XHandler 架构）
 *
 * @author noear
 * @since 1.0
 * */
public interface XHandler {
    void handle(XContext context) throws Throwable;
}
