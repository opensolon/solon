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
        test(ctx);
    }

    public boolean test(Context ctx) throws Throwable {
        if (AuthUtil.adapter().authProcessor() == null) {
            return true;
        }

        String path = ctx.pathNew().toLowerCase();

        //不需要验证
        if (path.equals(AuthUtil.adapter().loginUrl()) ||
                path.equals(AuthUtil.adapter().loginProcessingUrl()) ||
                path.equals(AuthUtil.adapter().logoutUrl())) {
            return true;
        }

        //不需要验证
        if (AuthUtil.adapter().authPathMatchers().test(ctx, path) == false) {
            return true;
        }

        //验证登录情况
        if (AuthUtil.adapter().authProcessor().verifyLogined() == false) {
            //未登录的，跳到登录页
            ctx.redirect(AuthUtil.adapter().loginUrl());
            ctx.setHandled(true);
            return true;
        }

        return false;
    }
}
