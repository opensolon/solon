package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthLogined;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 */
public class LoginedInterceptor extends AbstractInterceptor<AuthLogined> {
    private static final Logger log = LoggerFactory.getLogger(LoginedInterceptor.class);
    public static final LoginedInterceptor instance = new LoginedInterceptor();


    @Override
    public Class<AuthLogined> type() {
        return AuthLogined.class;
    }

    @Override
    public Result validate(AuthLogined anno) {
        try {
            if (AuthUtil.adapter().authProcessor().verifyLogined()) {
                return Result.succeed();
            } else {
                return Result.failure(401);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.failure(e.getMessage());
        }
    }
}
