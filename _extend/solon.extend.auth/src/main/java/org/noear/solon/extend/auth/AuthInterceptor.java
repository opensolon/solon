package org.noear.solon.extend.auth;

import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.*;
import org.noear.solon.extend.validation.ValidatorManager;

/**
 * 认证拦截器
 *
 * @author noear
 * @since 1.4
 */
public class AuthInterceptor implements Handler {
    AuthAdapter authAdapter;

    public AuthInterceptor() {
        Aop.getAsyn(AuthAdapter.class, bw -> {
            authAdapter = bw.raw();
        });
    }


    @Override
    public void handle(Context ctx) throws Throwable {
        String path = ctx.pathNew();

        //需要验证
        if (path.equals(authAdapter.loginUrl()) ||
                path.equals(authAdapter.loginProcessingUrl()) ||
                path.equals(authAdapter.logoutUrl())) {
            return;
        }

        //不需要验证
        if (authAdapter.verifyUrlMatchers().test(path) == false) {
            return;
        }

        //验证通过
        if (AuthProcessorProxy.getInstance().verifyUrl(path, ctx.method())) {
            return;
        }

        //验证失败的
        Result result = Result.failure(401, "Unauthorized");
        ValidatorManager.global().failureDo(ctx, null, result, result.getDescription());
    }
}
