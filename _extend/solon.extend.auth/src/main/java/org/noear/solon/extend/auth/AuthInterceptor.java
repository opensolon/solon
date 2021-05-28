package org.noear.solon.extend.auth;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.*;
import org.noear.solon.extend.validation.ValidatorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 认证拦截器
 *
 * @author noear
 * @since 1.4
 */
@Component
public class AuthInterceptor implements Handler {
    static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    AuthAdapter authAdapter;

    public AuthInterceptor() {
        Aop.getAsyn(AuthAdapter.class, bw -> {
            authAdapter = bw.raw();
        });
    }

    @Init
    public void init() {
        if (authAdapter == null) {
            log.warn("The AuthAdapter is not initialized.");
            return;
        }

        if (authAdapter.authProcessor() == null) {
            log.warn("The AuthProcessor is not initialized.");
            return;
        }
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        if (authAdapter == null) {
            return;
        }

        String path = ctx.pathNew().toLowerCase();

        //需要验证
        if (path.equals(authAdapter.loginUrl()) ||
                path.equals(authAdapter.loginProcessingUrl()) ||
                path.equals(authAdapter.logoutUrl())) {
            return;
        }

        //不需要验证
        if (authAdapter.authUrlMatchers().test(path) == false) {
            return;
        }

        //验证通过
        if (authAdapter.authProcessor().verifyUrl(path, ctx.method())) {
            return;
        }

        //验证失败的
        Result result = Result.failure(403, "Forbidden");
        ValidatorManager.global().failureDo(ctx, null, result, result.getDescription());
    }
}
