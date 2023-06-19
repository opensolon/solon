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
     * */
    boolean matched(Class<?> returnType);

    /**
     * 返回处理
     * */
    void returnHandle(Context ctx, Action action, Object returnValue) throws Throwable;
}
