package org.noear.solon.extend.security;

import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.extend.security.annotation.Logical;
import org.noear.solon.extend.security.model.UserModel;

/**
 * @author noear
 * @since 1.3
 */
public class AuthProviderProxy implements AuthProvider {
    private static AuthProvider instance;

    public static AuthProvider getInstance() {
        if (instance == null) {
            instance = new AuthProviderProxy();
        }
        return instance;
    }

    private AuthProvider real;

    private AuthProviderProxy() {
        Aop.getAsyn(AuthProvider.class, bw -> {
            real = bw.raw();
        });
    }


    @Override
    public boolean login(String username, String password) {
        return real.login(username, password);
    }

    @Override
    public boolean login(String token) {
        return real.login(token);
    }

    @Override
    public void loginLoad(UserModel user) {
        real.loginLoad(user);
    }

    @Override
    public boolean verifyLogined() {
        return real.verifyLogined();
    }

    @Override
    public boolean verifyPath(String path, MethodType methodType) {
        return real.verifyPath(path, methodType);
    }

    @Override
    public boolean verifyPermissions(String[] permissions, Logical logical) {
        return real.verifyPermissions(permissions, logical);
    }

    @Override
    public boolean verifyRoles(String[] roles, Logical logical) {
        return real.verifyRoles(roles, logical);
    }
}
