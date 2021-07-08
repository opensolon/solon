package org.noear.solon.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * 认证失败处理者
 *
 * @author noear
 * @since 1.4
 */
@FunctionalInterface
public interface AuthFailureHandler {
    /**
     * 失败时回调
     *
     * @param ctx 上下文
     * @param rst 认证结果
     * */
    void onFailure(Context ctx, Result rst) throws Throwable;
}
