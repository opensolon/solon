package org.noear.solon.core;

/**
 * 通用渲染接口
 *
 * @see XRenderManager#register(XRender)
 * @author noear
 * @since 1.0
 * */
public interface XRender {
    /**
     * 渲染
     *
     * @param data 数据
     * @param ctx  上下文
     */
    void render(Object data, XContext ctx) throws Throwable;

    /**
     * 渲染并返回（默认不实现）
     * */
    default String renderAndReturn(Object data, XContext ctx) throws Throwable {
        return null;
    }
}
