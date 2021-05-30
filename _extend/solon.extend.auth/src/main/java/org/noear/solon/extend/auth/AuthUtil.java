package org.noear.solon.extend.auth;

import org.noear.solon.core.Aop;
import org.noear.solon.extend.auth.annotation.Logical;

/**
 * @author noear
 * @since 1.4
 */
public class AuthUtil {
    private static AuthAdapter adapter = new AuthAdapter();

    static {
        //如果容器里有，优先用容器的
        Aop.getAsyn(AuthAdapter.class, bw -> {
            adapter = bw.raw();
        });
    }

    public static AuthAdapter adapter() {
        return adapter;
    }

    /**
     * 登录地址
     * */
    public static String loginUrl(){
        return adapter().loginUrl();
    }


    /**
     * 验证是否已登录
     * */
    public static boolean verifyLogined(){
        return adapter().authProcessor().verifyLogined();
    }

    /**
     * 验证是否有路径授权
     * */
    public static boolean verifyPath(String path, String method) {
        return adapter().authProcessor().verifyPath(path, method);
    }

    /**
     * 验证是否有权限授权
     * */
    public static boolean verifyPermissions(String... permissions) {
        return verifyPermissions(permissions, Logical.OR);
    }

    /**
     * 验证是否有权限授权(同时满足多个权限)
     * */
    public static boolean verifyPermissionsAnd(String... permissions) {
        return verifyPermissions(permissions, Logical.AND);
    }

    public static boolean verifyPermissions(String[] permissions, Logical logical) {
        return adapter().authProcessor().verifyPermissions(permissions, logical);
    }

    /**
     * 验证是否有角色授权
     * */
    public static boolean verifyRoles(String... roles) {
        return verifyRoles(roles, Logical.OR);
    }

    /**
     * 验证是否有角色授权(同时满足多个角色)
     * */
    public static boolean verifyRolesAnd(String... roles) {
        return verifyRoles(roles, Logical.AND);
    }

    public static boolean verifyRoles(String[] roles, Logical logical) {
        return adapter().authProcessor().verifyRoles(roles, logical);
    }
}
