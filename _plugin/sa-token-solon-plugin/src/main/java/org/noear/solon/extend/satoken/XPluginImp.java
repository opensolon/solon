package org.noear.solon.extend.satoken;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.satoken.aop.LoginValidator;
import org.noear.solon.extend.satoken.aop.PermissionValidator;
import org.noear.solon.extend.satoken.aop.RoleValidator;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanAroundAdd(SaCheckPermission.class, new PermissionValidator());
        Aop.context().beanAroundAdd(SaCheckRole.class, new RoleValidator());
        Aop.context().beanAroundAdd(SaCheckLogin.class, new LoginValidator());
    }
}
