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
@FunctionalInterface
public interface RouterInterceptor {
    /**
     * 执行拦截
     */
    void doIntercept(Context ctx,
                     @Nullable Handler mainHandler,
                     RouterInterceptorChain chain) throws Throwable;

    /**
     * 提交结果（action / render 执行前调用）
     */
    default Object postResult(Context ctx, @Nullable Object result) throws Throwable {
        return result;
    }
}
