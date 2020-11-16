package org.noear.solon.core.handle;

/**
 * 通用处理接口（实现：XContext + XHandler 架构）
 *
 * @author noear
 * @since 1.0
 * */
public interface Handler {
    void handle(Context context) throws Throwable;
}
