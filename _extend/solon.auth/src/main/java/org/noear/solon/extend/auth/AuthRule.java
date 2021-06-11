package org.noear.solon.extend.auth;

import org.noear.solon.core.handle.Handler;

/**
 * 授权规则
 *
 * @author noear
 * @since 1.4
 */
public interface AuthRule extends Handler {
    AuthRule include(String path);
    AuthRule exclude(String path);

    AuthRule verifyIp();
    AuthRule verifyLogined();
    AuthRule verifyPath();
    AuthRule verifyPermissions(String... permissions);
    AuthRule verifyPermissionsAnd(String... permissions);
    AuthRule verifyRoles(String... roles);
    AuthRule verifyRolesAnd(String... roles);

    AuthRule failure(AuthFailureHandler handler);
}
