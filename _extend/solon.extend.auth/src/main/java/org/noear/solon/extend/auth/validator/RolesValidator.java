package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 */
public class RolesValidator extends AbstractValidator<AuthRoles> {
    private static final Logger log = LoggerFactory.getLogger(RolesValidator.class);
    public static final RolesValidator instance = new RolesValidator();


    @Override
    public Class<AuthRoles> type() {
        return AuthRoles.class;
    }

    @Override
    public Result validate(AuthRoles anno) {
        try {
            if (AuthUtil.verifyRoles(anno.value(), anno.logical())) {
                return Result.succeed();
            } else {
                return Result.failure(403, "Forbidden");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.failure(e.getMessage());
        }
    }
}
