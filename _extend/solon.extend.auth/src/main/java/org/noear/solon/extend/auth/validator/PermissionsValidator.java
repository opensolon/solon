package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 */
public class PermissionsValidator extends AbstractValidator<AuthPermissions> {
    private static final Logger log = LoggerFactory.getLogger(PermissionsValidator.class);
    public static final PermissionsValidator instance = new PermissionsValidator();

    @Override
    public Class<AuthPermissions> type() {
        return AuthPermissions.class;
    }

    @Override
    public Result validate(AuthPermissions anno) {
        try {
            if (AuthUtil.verifyPermissions(anno.value(), anno.logical())) {
                return Result.succeed();
            } else {
                return Result.failure(403, AuthUtil.MESSAGE_OF_PERMISSIONS);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.failure(e.getMessage());
        }
    }
}
