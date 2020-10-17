package org.noear.solon.core;

/**
 * 通用渲染接口
 *
 * @author noear
 * @since 1.0
 * */
public interface XRender {
    /**
     * 渲染
     *
     * @param data 数据
     * @param ctx 上下文
     */
    void render(Object data, XContext ctx) throws Throwable;
}
