package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;

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
    void doIntercept(Context ctx, RouterInterceptorChain chain) throws Throwable;
}
