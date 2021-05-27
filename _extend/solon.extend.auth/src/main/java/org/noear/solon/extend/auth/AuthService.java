package org.noear.solon.extend.auth;

import org.noear.solon.extend.auth.annotation.Logical;
import org.noear.solon.extend.auth.model.Subject;

/**
 * 认证服务（需要用户对接）
 *
 * @author noear
 * @since 1.3
 */
public interface AuthService {
    /**
     * 用账号密码登录
     */
    boolean login(String username, String password);

    /**
     * 登录后加载模型（可用于自动登录）
     */
    void loginLoad(Subject subject);

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
