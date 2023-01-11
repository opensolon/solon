package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;

/**
 * 路由拦截器调用链
 *
 * @author noear
 * @since 1.12
 * */
public interface RouterInterceptorChain {
    /**
     * 拦截
     *
     * @param ctx 上下文
     * */
    void doIntercept(Context ctx, @Nullable Handler mainHandler) throws Throwable;
}
