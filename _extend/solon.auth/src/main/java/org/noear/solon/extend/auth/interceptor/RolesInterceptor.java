package org.noear.solon.extend.auth.interceptor;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthRoles;

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
