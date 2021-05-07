package org.noear.solon.extend.security.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.security.SecurityServiceProxy;
import org.noear.solon.extend.validation.Validator;

/**
 * @author noear
 * @since 1.3
 */
public class AuthRolesValidator implements Validator<AuthRoles> {
    public static final AuthRolesValidator instance = new AuthRolesValidator();


    @Override
    public String message(AuthRoles anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, AuthRoles anno, String name, StringBuilder tmp) {
        if (SecurityServiceProxy.getInstance().verifyRoles(anno.value(), anno.logical())) {
            return Result.succeed();
        } else {
            return Result.failure();
        }
    }
}
