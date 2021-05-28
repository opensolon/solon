package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthAdapter;
import org.noear.solon.extend.auth.annotation.AuthRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 */
public class RolesInterceptor extends AbstractInterceptor<AuthRoles> {
    private static final Logger log = LoggerFactory.getLogger(RolesInterceptor.class);
    public static final RolesInterceptor instance = new RolesInterceptor();


    @Override
    public Class<AuthRoles> type() {
        return AuthRoles.class;
    }

    @Override
    public Result validate(AuthRoles anno) {
        try {
            if (AuthAdapter.global().authProcessor().verifyRoles(anno.value(), anno.logical())) {
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
