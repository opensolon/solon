package org.noear.solon.extend.shiro;

import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.mgt.SecurityManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.shiro.aop.*;
import org.noear.solon.extend.shiro.config.ShiroConfiguration;
import org.noear.solon.extend.validation.ValidatorManager;

/**
 * @author noear
 * @since 1.3
 */
public class ShiroPluginImp implements Plugin {

    @SuppressWarnings("unchecked")
    @Override
    public void start(SolonApp app) {
        ValidatorManager.global().register(RequiresPermissions.class, PermissionAnnotationInterceptor.instance);
        ValidatorManager.global().register(RequiresRoles.class, RoleAnnotationInterceptor.instance);
        ValidatorManager.global().register(RequiresUser.class, UserAnnotationInterceptor.instance);
        ValidatorManager.global().register(RequiresGuest.class, GuestAnnotationInterceptor.instance);
        ValidatorManager.global().register(RequiresAuthentication.class, AuthenticateAnnotationInterceptor.instance);

        app.beanScan(ShiroPluginImp.class);

        Aop.wrapAndPut(SecurityManager.class, new ShiroConfiguration().securityManager());
    }
}
