package org.noear.solon.auth;

import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 提供手动授权控制支持
 *
 * @author noear
 * @since 1.4
 */
public class AuthUtil {
    public static final String MESSAGE_OF_IP = " , this ip unauthorized";
    public static final String MESSAGE_OF_LOGINED = "Unauthorized";
    public static final String MESSAGE_OF_PATH = "Forbidden";
    public static final String MESSAGE_OF_PERMISSIONS = "No permission granted";
    public static final String MESSAGE_OF_ROLES = "No role granted";

    private static AuthAdapter adapter = new AuthAdapter();
    private static List<AuthAdapterSupplier> adapterSuppliers = new ArrayList<>();

    static {
        //如果容器里有，优先用容器的
        Aop.getAsyn(AuthAdapter.class, bw -> {
            adapter = bw.raw();
        });
    }

    public static AuthAdapter adapter() {
        if (adapterSuppliers.size() >0) {
            Context ctx = Context.current();
            for(AuthAdapterSupplier a1 : adapterSuppliers){
                if(ctx.pathNew().startsWith(a1.pathPrefix())){
                    return a1.adapter();
                }
            }
        }

        return adapter;
    }

    public static void adapterAdd(AuthAdapterSupplier supplier) {
        adapterSuppliers.add(supplier);
        //排除，短的在后（用负数反一反）
        adapterSuppliers.sort(Comparator.comparingInt(e -> -e.pathPrefix().length()));
    }

    public static void adapterRemove(AuthAdapterSupplier supplier) {
        adapterSuppliers.remove(supplier);
    }

    /**
     * 验证是否有Ip授权
     */
    public static boolean verifyIp(String ip) {
        return adapter().processor().verifyIp(ip);
    }

    /**
     * 验证是否已登录
     */
    public static boolean verifyLogined() {
        return adapter().processor().verifyLogined();
    }

    /**
     * 验证是否有路径授权
     *
     * @param path 路径
     * @param method 请求方式
     */
    public static boolean verifyPath(String path, String method) {
        //先验证是否有登录
        if (verifyLogined()) {
            return adapter().processor().verifyPath(path, method);
        } else {
            return false;
        }
    }

    /**
     * 验证是否有权限授权
     *
     * @param permissions 权限
     */
    public static boolean verifyPermissions(String... permissions) {
        return verifyPermissions(permissions, Logical.OR);
    }

    /**
     * 验证是否有权限授权(同时满足多个权限)
     *
     * @param permissions 权限
     */
    public static boolean verifyPermissionsAnd(String... permissions) {
        return verifyPermissions(permissions, Logical.AND);
    }

    /**
     * 验证是否有权限授权
     *
     * @param permissions 权限
     * @param logical 验证的逻辑关系
     */
    public static boolean verifyPermissions(String[] permissions, Logical logical) {
        //先验证是否有登录
        if (verifyLogined()) {
            return adapter().processor().verifyPermissions(permissions, logical);
        } else {
            return false;
        }
    }

    /**
     * 验证是否有角色授权
     *
     * @param roles 角色
     */
    public static boolean verifyRoles(String... roles) {
        return verifyRoles(roles, Logical.OR);
    }

    /**
     * 验证是否有角色授权(同时满足多个角色)
     *
     * @param roles 角色
     */
    public static boolean verifyRolesAnd(String... roles) {
        return verifyRoles(roles, Logical.AND);
    }

    /**
     * 验证是否有角色授权
     *
     * @param roles 角色
     * @param logical 验证的逻辑关系
     */
    public static boolean verifyRoles(String[] roles, Logical logical) {
        //先验证是否有登录
        if (verifyLogined()) {
            return adapter().processor().verifyRoles(roles, logical);
        } else {
            return false;
        }
    }
}
