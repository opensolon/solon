package org.noear.solon.auth;

import org.noear.solon.SolonApp;
import org.noear.solon.auth.annotation.*;
import org.noear.solon.auth.interceptor.*;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanAroundAdd(AuthIp.class, new IpInterceptor());
        Aop.context().beanAroundAdd(AuthLogined.class, new LoginedInterceptor());
        Aop.context().beanAroundAdd(AuthPath.class, new PathInterceptor());
        Aop.context().beanAroundAdd(AuthPermissions.class, new PermissionsInterceptor());
        Aop.context().beanAroundAdd(AuthRoles.class, new RolesInterceptor());
    }
}
