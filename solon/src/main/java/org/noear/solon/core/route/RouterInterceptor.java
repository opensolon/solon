package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;

/**
 * 路由拦截器
 *
 * @author noear
 * @since 1.12
 */
public interface RouterInterceptor {
    /**
     * 拦截
     * */
    void doIntercept(Context ctx,
                     @Nullable Handler mainHandler,
                     RouterInterceptorChain chain) throws Throwable;
}
