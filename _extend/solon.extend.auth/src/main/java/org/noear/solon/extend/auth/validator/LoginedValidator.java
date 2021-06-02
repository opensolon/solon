package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthLogined;

/**
 * @author noear
 * @since 1.3
 */
public class LoginedValidator extends AbstractValidator<AuthLogined> {
    public static final LoginedValidator instance = new LoginedValidator();

    @Override
    public Class<AuthLogined> type() {
        return AuthLogined.class;
    }

    @Override
    public Result validate(AuthLogined anno) throws Exception {
        if (AuthUtil.verifyLogined()) {
            return Result.succeed();
        } else {
            return Result.failure(401, AuthUtil.MESSAGE_OF_LOGINED);
        }
    }
}
