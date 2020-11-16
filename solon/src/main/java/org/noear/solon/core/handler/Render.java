package org.noear.solon.core.handler;

/**
 * 通用渲染接口
 *
 * @see RenderManager#register(Render)
 * @author noear
 * @since 1.0
 * */
public interface Render {
    /**
     * 渲染
     *
     * @param data 数据
     * @param ctx  上下文
     */
    void render(Object data, Context ctx) throws Throwable;

    /**
     * 渲染并返回（默认不实现）
     * */
    default String renderAndReturn(Object data, Context ctx) throws Throwable {
        return null;
    }
}
