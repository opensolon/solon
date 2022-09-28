package org.noear.solon.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * 认证失败处理者-默认实现
 *
 * @author noear
 * @since 1.10
 */
public class AuthFailureHandlerDefault implements AuthFailureHandler {
    @Override
    public void onFailure(Context ctx, Result rst) throws Throwable {
        throw new AuthException(rst.getCode(), rst.getDescription());
    }
}
