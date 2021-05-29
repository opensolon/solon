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
        if (AuthAdapter.global().authProcessor() == null) {
            return true;
        }

        String url = ctx.pathNew().toLowerCase();

        //不需要验证
        if (url.equals(AuthAdapter.global().loginUrl()) ||
                url.equals(AuthAdapter.global().loginProcessingUrl()) ||
                url.equals(AuthAdapter.global().logoutUrl())) {
            return true;
        }

        //不需要验证
        if (AuthAdapter.global().authUrlMatchers().test(ctx, url) == false) {
            return true;
        }

        //验证登录情况
        if (AuthAdapter.global().authProcessor().verifyLogined() == false) {
            //未登录的，跳到登录页
            ctx.redirect(AuthAdapter.global().loginUrl());
            ctx.setHandled(true);
            return true;
        }

        return false;
    }
}
