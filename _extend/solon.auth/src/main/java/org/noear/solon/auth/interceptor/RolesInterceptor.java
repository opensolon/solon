package org.noear.solon.auth.interceptor;

import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.AuthRoles;
import org.noear.solon.core.handle.Result;

/**
 * AuthRoles 注解拦截器
 *
 * @author noear
 * @since 1.3
 */
public class RolesInterceptor extends AbstractInterceptor<AuthRoles> {
    @Override
    public Class<AuthRoles> type() {
        return AuthRoles.class;
    }

    @Override
    public Result verify(AuthRoles anno) throws Exception {
        if (AuthUtil.verifyRoles(anno.value(), anno.logical())) {
            return Result.succeed();
        } else {
            return Result.failure(403, AuthUtil.MESSAGE_OF_ROLES);
        }
    }
}
