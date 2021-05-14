package org.noear.solon.extend.shiro;

import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.mgt.SecurityManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.shiro.aop.*;
import org.noear.solon.extend.shiro.config.ShiroConfiguration;

/**
 * @author noear
 * @since 1.3
 */
public class ShiroPluginImp implements Plugin {

    @SuppressWarnings("unchecked")
    @Override
    public void start(SolonApp app) {
        //换了种方式，通过拦截器获得一级拦截权限（之前的方案，需要类上有：@Valid 注解；借助它的拦截，获得二级执行权限有）
        //
        Aop.context().beanInterceptorAdd(RequiresPermissions.class, PermissionAnnotationInterceptor.instance);
        Aop.context().beanInterceptorAdd(RequiresRoles.class, RoleAnnotationInterceptor.instance);
        Aop.context().beanInterceptorAdd(RequiresUser.class, UserAnnotationInterceptor.instance);
        Aop.context().beanInterceptorAdd(RequiresGuest.class, GuestAnnotationInterceptor.instance);
        Aop.context().beanInterceptorAdd(RequiresAuthentication.class, AuthenticateAnnotationInterceptor.instance);

        //这个不需要，因为这个插件里，没有注解类
        //
        //app.beanScan(ShiroPluginImp.class);

        //把SecurityManager注入到solon bean 容器，可能还没法用；shiro框架，不会自动去拿；得找个地方，把它与shiro框架对接上
        //
        Aop.wrapAndPut(SecurityManager.class, new ShiroConfiguration().securityManager());
    }
}
