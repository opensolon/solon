package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthAdapter;
import org.noear.solon.extend.auth.annotation.AuthRoles;
import org.noear.solon.extend.validation.Validator;

/**
 * @author noear
 * @since 1.3
 */
public class AuthRolesValidator implements Validator<AuthRoles> {
    public static final AuthRolesValidator instance = new AuthRolesValidator();

    AuthAdapter authAdapter;

    public AuthRolesValidator() {
        Aop.getAsyn(AuthAdapter.class, bw -> {
            authAdapter = bw.raw();
        });
    }

    @Override
    public String message(AuthRoles anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, AuthRoles anno, String name, StringBuilder tmp) {
        if (authAdapter.authProcessor().verifyRoles(anno.value(), anno.logical())) {
            return Result.succeed();
        } else {
            return Result.failure(401);
        }
    }
}
