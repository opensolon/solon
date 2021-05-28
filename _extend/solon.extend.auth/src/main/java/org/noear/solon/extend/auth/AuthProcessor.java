package org.noear.solon.extend.auth;

import org.noear.solon.extend.auth.annotation.Logical;

/**
 * 认证处理器（需要用户对接）
 *
 * @author noear
 * @since 1.3
 */
public interface AuthProcessor {

    /**
     * 验证登录状态
     */
    boolean verifyLogined();

    /**
     * 验证路径（一般使用路径验证）
     */
    boolean verifyUrl(String url, String method);

    /**
     * 验证特定权限（有特殊情况用权限验证）
     */
    boolean verifyPermissions(String[] permissions, Logical logical);

    /**
     * 验证特定角色（有特殊情况用角色验证）
     */
    boolean verifyRoles(String[] roles, Logical logical);
}
