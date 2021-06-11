package org.noear.solon.extend.auth.interceptor;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthLogined;

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
            return Result.failure(401, AuthUtil.MESSAGE_OF_LOGINED);
        }
    }
}
