package org.noear.solon.extend.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.shiro.aop.*;
import org.noear.solon.extend.shiro.config.ShiroConfiguration;

/**
 * @author tomsun28
 * @since 2021/5/12 23:20
 */
public class ShiroPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //换了种方式，注册AOP环绕处理，获得一级拦截权限（之前的方案，需要类上有：@Valid 注解；借助它的拦截，获得二级执行权限有）
        //
        Aop.context().beanAroundAdd(RequiresPermissions.class, PermissionInterceptor.instance);
        Aop.context().beanAroundAdd(RequiresRoles.class, RoleInterceptor.instance);
        Aop.context().beanAroundAdd(RequiresUser.class, UserInterceptor.instance);
        Aop.context().beanAroundAdd(RequiresGuest.class, GuestInterceptor.instance);
        Aop.context().beanAroundAdd(RequiresAuthentication.class, AuthenticateInterceptor.instance);

        //这个不需要，因为这个插件里，没有注解类
        //
        //app.beanScan(ShiroPluginImp.class);

        //把SecurityManager注入到solon bean 容器，可能还没法用；shiro框架，不会自动去拿；把它与shiro框架对接上
        //
        //Aop.wrapAndPut(SecurityManager.class, new ShiroConfiguration().securityManager());

        //相对于 beanScan，beanMake 是处理单类的
        app.beanMake(ShiroConfiguration.class);
    }
}
