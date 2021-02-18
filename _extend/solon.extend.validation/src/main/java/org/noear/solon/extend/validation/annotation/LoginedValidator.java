package org.noear.solon.extend.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.3
 * */
public class LoginedValidator implements Validator<Logined> {
    public static final LoginedValidator instance = new LoginedValidator();

    public static String sessionUserKeyName = "user_id";

    @Override
    public String message(Logined anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, Logined anno, String name, StringBuilder tmp) {
        String userKeyName = anno.value();
        if(Utils.isEmpty(userKeyName)){
            userKeyName = LoginedValidator.sessionUserKeyName;
        }

        Object userKey = ctx.session(userKeyName);

        if (userKey == null) {
            return Result.failure();
        }

        if (userKey instanceof Number) {
            if (((Number) userKey).longValue() < 1) {
                return Result.failure();
            }
        }

        if (userKey instanceof String) {
            if (((String) userKey).length() < 1) {
                return Result.failure();
            }
        }

        return Result.succeed();
    }
}
