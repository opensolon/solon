package org.noear.solon.extend.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * 认证拦截器
 *
 * @author noear
 * @since 1.4
 */
public class AuthInterceptorPath extends AuthInterceptorLogined {

    @Override
    public void handle(Context ctx) throws Throwable {
        String path = ctx.pathNew().toLowerCase();

        if (test(ctx, path)) {
            return;
        }

        //验证地址权限
        if (AuthUtil.adapter().authProcessor().verifyPath(path, ctx.method()) == false) {
            //验证失败的
            Result result = Result.failure(403, "Forbidden");
            AuthUtil.adapter().authOnFailure().accept(ctx, result);
            ctx.setHandled(true);
        }
    }
}
