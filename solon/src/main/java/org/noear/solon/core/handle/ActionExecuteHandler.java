package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodWrap;

/**
 * 动作执行处理。用于支持多种消息体执行
 *
 * @see Action#callDo(Context, Object, MethodWrap)
 * @author noear
 * @since 1.0
 * */
public interface ActionExecuteHandler {
    /**
     * 是否匹配
     *
     * @param ctx         上下文
     * @param contentType 内容类型
     */
    boolean matched(Context ctx, String contentType);

    /**
     * 参数分析
     *
     * @param ctx    上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    Object[] resolveArguments(Context ctx, Object target, MethodWrap mWrap) throws Throwable;

    /**
     * 执行
     *
     * @param ctx    上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    Object executeHandle(Context ctx, Object target, MethodWrap mWrap) throws Throwable;
}
