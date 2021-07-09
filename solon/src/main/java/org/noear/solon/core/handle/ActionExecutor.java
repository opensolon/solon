package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodWrap;

/**
 * 动作执行器。用于支持多种消息体执行
 *
 * @see Action#callDo(Context, Object, MethodWrap)
 * @author noear
 * @since 1.0
 * */
public interface ActionExecutor {
    /**
     * 是否匹配
     *
     * @param ctx 上下文
     * @param ct 内容类型
     * */
    boolean matched(Context ctx, String ct);

    /**
     * 执行
     *
     * @param ctx 上下文
     * @param obj 控制器
     * @param mWrap 函数包装器
     * */
    Object execute(Context ctx, Object obj, MethodWrap mWrap) throws Throwable;

}
