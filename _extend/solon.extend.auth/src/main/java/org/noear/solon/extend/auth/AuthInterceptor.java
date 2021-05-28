package org.noear.solon.extend.auth;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.*;
import org.noear.solon.extend.validation.ValidatorManager;


/**
 * 认证拦截器
 *
 * @author noear
 * @since 1.4
 */
@Component
public class AuthInterceptor implements Handler {

    @Override
    public void handle(Context ctx) throws Throwable {
        if (AuthAdapter.global().authProcessor() == null) {
            return;
        }

        String path = ctx.pathNew().toLowerCase();

        //不需要验证
        if (path.equals(AuthAdapter.global().loginUrl()) ||
                path.equals(AuthAdapter.global().loginProcessingUrl()) ||
                path.equals(AuthAdapter.global().logoutUrl())) {
            return;
        }

        //不需要验证
        if (AuthAdapter.global().authUrlMatchers().test(path) == false) {
            return;
        }

        //验证登录情况
        if (AuthAdapter.global().authProcessor().verifyLogined() == false) {
            //未登录的，跳到登录页
            ctx.redirect(AuthAdapter.global().loginUrl());
            ctx.setHandled(true);
            return;
        }

        //验证地址权限
        if (AuthAdapter.global().authProcessor().verifyUrl(path, ctx.method()) == false) {
            //验证失败的
            Result result = Result.failure(403, "Forbidden");
            ValidatorManager.global().failureDo(ctx, null, result, result.getDescription());
        }
    }
}
