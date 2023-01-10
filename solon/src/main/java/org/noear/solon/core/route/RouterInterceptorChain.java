package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;

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
    void doIntercept(Context ctx) throws Throwable;
}
