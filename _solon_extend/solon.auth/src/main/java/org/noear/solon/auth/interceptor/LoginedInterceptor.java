package org.noear.solon.auth.interceptor;

import org.noear.solon.auth.AuthStatus;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.AuthLogined;
import org.noear.solon.core.handle.Result;

/**
 * AuthLogined 注解拦截器
 *
 * @author noear
 * @since 1.3
 */
public class LoginedInterceptor extends AbstractInterceptor<AuthLogined> {
    @Override
    public Class<AuthLogined> type() {
        return AuthLogined.class;
    }

    @Override
    public Result verify(AuthLogined anno) throws Exception {
        if (AuthUtil.verifyLogined()) {
            return Result.succeed();
        } else {
            return AuthStatus.OF_LOGINED.toResult();
        }
    }
}
