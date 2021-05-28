package org.noear.solon.extend.auth;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.auth.annotation.*;
import org.noear.solon.extend.auth.validator.AuthPermissionsValidator;
import org.noear.solon.extend.auth.validator.AuthRolesValidator;
import org.noear.solon.extend.validation.ValidatorManager;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //添加前置拦截器
        app.before(Aop.get(AuthInterceptor.class));

        //添加注解验证器
        ValidatorManager.global().register(AuthPermissions.class, AuthPermissionsValidator.instance);
        ValidatorManager.global().register(AuthRoles.class, AuthRolesValidator.instance);
    }
}
