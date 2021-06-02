package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthRoles;

/**
 * @author noear
 * @since 1.3
 */
public class RolesValidator extends AbstractValidator<AuthRoles> {
    public static final RolesValidator instance = new RolesValidator();

    @Override
    public Class<AuthRoles> type() {
        return AuthRoles.class;
    }

    @Override
    public Result validate(AuthRoles anno) throws Exception {
        if (AuthUtil.verifyRoles(anno.value(), anno.logical())) {
            return Result.succeed();
        } else {
            return Result.failure(403, AuthUtil.MESSAGE_OF_ROLES);
        }
    }
}
