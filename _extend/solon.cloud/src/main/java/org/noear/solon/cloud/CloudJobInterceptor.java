package org.noear.solon.cloud;

import org.noear.solon.core.handle.Context;

/**
 * 云工作拦截器
 *
 * @author noear
 * @since 1.6
 */
public interface CloudJobInterceptor {
    void doInterceptor(Context ctx, CloudJobHandler handler) throws Throwable;
}
