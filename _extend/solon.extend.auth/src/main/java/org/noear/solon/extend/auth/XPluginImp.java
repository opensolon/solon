package org.noear.solon.extend.auth;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.auth.annotation.AuthLogined;
import org.noear.solon.extend.auth.annotation.AuthPath;
import org.noear.solon.extend.auth.annotation.AuthPermissions;
import org.noear.solon.extend.auth.annotation.AuthRoles;
import org.noear.solon.extend.auth.interceptor.LoginedInterceptor;
import org.noear.solon.extend.auth.interceptor.PathInterceptor;
import org.noear.solon.extend.auth.interceptor.PermissionsInterceptor;
import org.noear.solon.extend.auth.interceptor.RolesInterceptor;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanAroundAdd(AuthLogined.class, new LoginedInterceptor());
        Aop.context().beanAroundAdd(AuthPath.class, new PathInterceptor());
        Aop.context().beanAroundAdd(AuthPermissions.class, new PermissionsInterceptor());
        Aop.context().beanAroundAdd(AuthRoles.class, new RolesInterceptor());
    }
}
