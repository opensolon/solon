package org.noear.solon.extend.auth;

import org.noear.solon.core.Aop;
import org.noear.solon.extend.auth.annotation.Logical;
import org.noear.solon.extend.auth.model.Subject;

/**
 * @author noear
 * @since 1.3
 */
public class AuthServiceProxy implements AuthService {
    private static AuthService instance;

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthServiceProxy();
        }
        return instance;
    }

    private AuthService real;

    private AuthServiceProxy() {
        Aop.getAsyn(AuthService.class, bw -> {
            real = bw.raw();
        });
    }


    @Override
    public boolean login(String username, String password) {
        return real.login(username, password);
    }

    @Override
    public void loginLoad(Subject user) {
        real.loginLoad(user);
    }

    @Override
    public boolean verifyLogined() {
        return real.verifyLogined();
    }

    @Override
    public boolean verifyUrl(String url, String method) {
        return real.verifyUrl(url, method);
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
