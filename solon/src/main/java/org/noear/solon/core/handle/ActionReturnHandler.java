package org.noear.solon.core.handle;

/**
 * 动作返回处理器
 *
 * @author noear
 * @since 2.3
 */
public interface ActionReturnHandler {
    /**
     * 是否匹配
     *
     * @param returnType 返回类型
     */
    boolean matched(Class<?> returnType);

    /**
     * 返回处理
     *
     * @param ctx         上下文
     * @param action      动作
     * @param returnValue 返回值
     */
    void returnHandle(Context ctx, Action action, Object returnValue) throws Throwable;
}
