package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthPermissions;

/**
 * @author noear
 * @since 1.3
 */
public class PermissionsValidator extends AbstractValidator<AuthPermissions> {
    public static final PermissionsValidator instance = new PermissionsValidator();

    @Override
    public Class<AuthPermissions> type() {
        return AuthPermissions.class;
    }

    @Override
    public Result validate(AuthPermissions anno) throws Exception {
        if (AuthUtil.verifyPermissions(anno.value(), anno.logical())) {
            return Result.succeed();
        } else {
            return Result.failure(403, AuthUtil.MESSAGE_OF_PERMISSIONS);
        }
    }
}
