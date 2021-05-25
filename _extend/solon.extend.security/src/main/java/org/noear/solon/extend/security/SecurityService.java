package org.noear.solon.extend.security;

import org.noear.solon.core.handle.MethodType;
import org.noear.solon.extend.security.annotation.Logical;
import org.noear.solon.extend.security.model.UserModel;

/**
 * @author noear
 * @since 1.3
 */
public interface SecurityService {
    /**
     * 用账号密码登录
     * */
    boolean login(String username, String password);

    /**
     * 用领牌登录
     * */
    boolean login(String token);

    /**
     * 登录后加载模型（可用于自动登录）
     * */
    void loginLoad(UserModel user);

    /**
     * 验证登录状态
     * */
    boolean verifyLogined();

    /**
     * 验证路径（一般使用路径验证）
     * */
    boolean verifyPath(String path, MethodType methodType);

    /**
     * 验证权限（有特殊情况用权限验证）
     * */
    boolean verifyPermissions(String[] permissions, Logical logical);

    /**
     * 验证角色（有特殊情况用角色验证）
     * */
    boolean verifyRoles(String[] roles, Logical logical);

}
