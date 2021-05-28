package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthAdapter;
import org.noear.solon.extend.auth.annotation.AuthPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 */
public class PermissionsInterceptor extends AbstractInterceptor<AuthPermissions> {
    private static final Logger log = LoggerFactory.getLogger(PermissionsInterceptor.class);
    public static final PermissionsInterceptor instance = new PermissionsInterceptor();

    @Override
    public Class<AuthPermissions> type() {
        return AuthPermissions.class;
    }

    @Override
    public Result validate(AuthPermissions anno) {
        try {
            if (AuthAdapter.global().authProcessor().verifyPermissions(anno.value(), anno.logical())) {
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
