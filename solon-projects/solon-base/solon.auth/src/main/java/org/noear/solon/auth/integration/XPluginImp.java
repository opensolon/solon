package org.noear.solon.auth.integration;

import org.noear.solon.auth.AuthAdapterSupplier;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.*;
import org.noear.solon.auth.interceptor.*;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        context.beanInterceptorAdd(AuthIp.class, new IpInterceptor());
        context.beanInterceptorAdd(AuthLogined.class, new LoginedInterceptor());
        context.beanInterceptorAdd(AuthPath.class, new PathInterceptor());
        context.beanInterceptorAdd(AuthPermissions.class, new PermissionsInterceptor());
        context.beanInterceptorAdd(AuthRoles.class, new RolesInterceptor());

        context.subBeansOfType(AuthAdapterSupplier.class, e -> AuthUtil.adapterAdd(e));
    }
}
