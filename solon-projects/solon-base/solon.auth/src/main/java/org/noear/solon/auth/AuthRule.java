package org.noear.solon.auth;

import org.noear.solon.core.handle.Handler;

/**
 * 授权规则
 *
 * @author noear
 * @since 1.4
 */
public interface AuthRule extends Handler {
    /**
     * 包函
     *
     * @param path 路径
     * */
    AuthRule include(String path);

    /**
     * 排除
     *
     * @param path 路径
     * */
    AuthRule exclude(String path);

    /**
     * 验证Ip
     * */
    AuthRule verifyIp();
    /**
     * 验证登录状态
     * */
    AuthRule verifyLogined();
    /**
     * 验证路径
     * */
    AuthRule verifyPath();
    /**
     * 验证权限
     *
     * @param permissions 权限
     * */
    AuthRule verifyPermissions(String... permissions);
    /**
     * 验证权限（并且关系）
     *
     * @param permissions 权限
     * */
    AuthRule verifyPermissionsAnd(String... permissions);
    /**
     * 验证角色
     *
     * @param roles 角色
     * */
    AuthRule verifyRoles(String... roles);
    /**
     * 验证角色（并且关系）
     *
     * @param roles 角色
     * */
    AuthRule verifyRolesAnd(String... roles);

    /**
     * 失败事件
     * */
    AuthRule failure(AuthFailureHandler handler);
}
