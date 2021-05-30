package org.noear.solon.extend.auth;

import org.noear.solon.core.handle.*;


/**
 * 认证拦截器
 *
 * @author noear
 * @since 1.4
 */
public class AuthInterceptorLogined implements AuthInterceptor {

    @Override
    public void handle(Context ctx) throws Throwable {
        String path = ctx.pathNew().toLowerCase();

        test(ctx, path);
    }

    protected boolean test(Context ctx, String path) throws Throwable {

        //不需要验证
        if (path.equals(AuthUtil.adapter().loginUrl())) {
            return true;
        }

        //不需要验证
        if (AuthUtil.adapter().authPathMatchers().test(ctx, path) == false) {
            return true;
        }

        //验证登录情况
        if (AuthUtil.adapter().authProcessor().verifyLogined() == false) {
            //未登录的，跳到登录页
            if (AuthUtil.adapter().loginUrl() == null) {
                ctx.statusSet(401);
                ctx.setHandled(true);
            } else {
                ctx.redirect(AuthUtil.adapter().loginUrl());
                ctx.setHandled(true);
            }
            return true;
        }

        return false;
    }
}
