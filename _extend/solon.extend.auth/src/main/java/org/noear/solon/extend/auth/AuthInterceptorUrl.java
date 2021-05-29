package org.noear.solon.extend.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;


/**
 * 认证拦截器
 *
 * @author noear
 * @since 1.4
 */
public class AuthInterceptorUrl extends AuthInterceptorLogined {

    @Override
    public void handle(Context ctx) throws Throwable {
        if (test(ctx)) {
            return;
        }

        String path = ctx.pathNew().toLowerCase();

        //验证地址权限
        if (AuthUtil.adapter().authProcessor().verifyPath(path, ctx.method()) == false) {
            //验证失败的
            Result result = Result.failure(403, "Sorry, no permission!");
            AuthUtil.adapter().authOnFailure().accept(ctx, result);
        }
    }
}
