package org.noear.solon.extend.shiro;

import org.apache.shiro.authz.annotation.*;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.shiro.aop.*;
import org.noear.solon.extend.shiro.config.ShiroConfiguration;

/**
 * @author tomsun28
 * @since 2021/5/12 23:20
 */
public class ShiroPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        //换了种方式，注册AOP环绕处理，获得一级拦截权限（之前的方案，需要类上有：@Valid 注解；借助它的拦截，获得二级执行权限有）
        //
        context.beanAroundAdd(RequiresPermissions.class, PermissionInterceptor.instance);
        context.beanAroundAdd(RequiresRoles.class, RoleInterceptor.instance);
        context.beanAroundAdd(RequiresUser.class, UserInterceptor.instance);
        context.beanAroundAdd(RequiresGuest.class, GuestInterceptor.instance);
        context.beanAroundAdd(RequiresAuthentication.class, AuthenticateInterceptor.instance);

        //这个不需要，因为这个插件里，没有大片的类要扫描
        //
        //app.beanScan(ShiroPluginImp.class);

        //把SecurityManager注入到solon bean 容器，可能还没法用；shiro框架，不会自动去拿；把它与shiro框架对接上
        //
        //Aop.wrapAndPut(SecurityManager.class, new ShiroConfiguration().securityManager());

        //相对于 beanScan，beanMake 是处理单类的
        context.beanMake(ShiroConfiguration.class);
    }
}
