package org.noear.solon.auth;

import org.noear.solon.auth.annotation.Logical;

/**
 * 认证处理器（由用户对接）
 *
 * @author noear
 * @since 1.3
 */
public interface AuthProcessor {

    /**
     * 验证IP
     * */
    boolean verifyIp(String ip);

    /**
     * 验证登录状态
     */
    boolean verifyLogined();

    /**
     * 验证路径（一般使用路径验证）
     *
     * @param path 路径
     * @param method 请求方式
     */
    boolean verifyPath(String path, String method);

    /**
     * 验证特定权限（有特殊情况用权限验证）
     *
     * @param permissions 权限
     * @param logical 认证的逻辑关系
     */
    boolean verifyPermissions(String[] permissions, Logical logical);

    /**
     * 验证特定角色（有特殊情况用角色验证）
     *
     * @param roles 角色
     * @param logical 认证的逻辑关系
     */
    boolean verifyRoles(String[] roles, Logical logical);
}
