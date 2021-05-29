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

        String url = ctx.pathNew().toLowerCase();

        //不需要验证
        if (url.equals(AuthUtil.adapter().loginUrl()) ||
                url.equals(AuthUtil.adapter().loginProcessingUrl()) ||
                url.equals(AuthUtil.adapter().logoutUrl())) {
            return true;
        }

        //不需要验证
        if (AuthUtil.adapter().authPathMatchers().test(ctx, url) == false) {
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
