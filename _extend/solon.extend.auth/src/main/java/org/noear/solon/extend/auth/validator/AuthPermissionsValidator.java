package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthAdapter;
import org.noear.solon.extend.auth.annotation.AuthPermissions;
import org.noear.solon.extend.validation.Validator;

/**
 * @author noear
 * @since 1.3
 */
public class AuthPermissionsValidator implements Validator<AuthPermissions> {
    public static final AuthPermissionsValidator instance = new AuthPermissionsValidator();

    AuthAdapter authAdapter;

    public AuthPermissionsValidator() {
        Aop.getAsyn(AuthAdapter.class, bw -> {
            authAdapter = bw.raw();
        });
    }

    @Override
    public String message(AuthPermissions anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, AuthPermissions anno, String name, StringBuilder tmp) {
        if (authAdapter.authProcessor().verifyPermissions(anno.value(), anno.logical())) {
            return Result.succeed();
        } else {
            return Result.failure(401);
        }
    }
}
