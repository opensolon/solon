package org.noear.solon.extend.auth.interceptor;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthPermissions;

/**
 * AuthPermissions 注解拦截器
 *
 * @author noear
 * @since 1.3
 */
public class PermissionsInterceptor extends AbstractInterceptor<AuthPermissions> {
    @Override
    public Class<AuthPermissions> type() {
        return AuthPermissions.class;
    }

    @Override
    public Result verify(AuthPermissions anno) throws Exception {
        if (AuthUtil.verifyPermissions(anno.value(), anno.logical())) {
            return Result.succeed();
        } else {
            return Result.failure(403, AuthUtil.MESSAGE_OF_PERMISSIONS);
        }
    }
}
