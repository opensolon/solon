package org.noear.solon.core.handle;

/**
 * 通用渲染接口
 *
 * @see RenderManager#register(Render)
 * @author noear
 * @since 1.0
 * */
public interface Render {
    /**
     * 获取名称
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 是否匹配
     *
     * @param ctx  上下文
     * @param accept 接收类型
     */
    default boolean matched(Context ctx, String accept) {
        return false;
    }

    /**
     * 渲染并返回（默认不实现）
     */
    default String renderAndReturn(Object data, Context ctx) throws Throwable {
        return null;
    }

    /**
     * 渲染
     *
     * @param data 数据
     * @param ctx  上下文
     */
    void render(Object data, Context ctx) throws Throwable;

}
