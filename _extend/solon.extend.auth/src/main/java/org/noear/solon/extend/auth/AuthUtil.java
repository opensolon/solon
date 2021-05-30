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

    public boolean hasPath(String path, String method) {
        return adapter().authProcessor().verifyPath(path, method);
    }

    public boolean hasPermissionsOr(String... permissions) {
        return adapter().authProcessor().verifyPermissions(permissions, Logical.OR);
    }

    public boolean hasPermissionsAnd(String... permissions) {
        return adapter().authProcessor().verifyPermissions(permissions, Logical.AND);
    }

    public boolean hasRolesOr(String... roles) {
        return adapter().authProcessor().verifyRoles(roles, Logical.OR);
    }

    public boolean hasRolesAnd(String... roles) {
        return adapter().authProcessor().verifyRoles(roles, Logical.AND);
    }
}
