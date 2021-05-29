package org.noear.solon.extend.auth;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.auth.annotation.AuthLogined;
import org.noear.solon.extend.auth.annotation.AuthPermissions;
import org.noear.solon.extend.auth.annotation.AuthRoles;
import org.noear.solon.extend.auth.validator.LoginedValidator;
import org.noear.solon.extend.auth.validator.PermissionsValidator;
import org.noear.solon.extend.auth.validator.RolesValidator;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanAroundAdd(AuthPermissions.class, PermissionsValidator.instance);
        Aop.context().beanAroundAdd(AuthRoles.class, RolesValidator.instance);
        Aop.context().beanAroundAdd(AuthLogined.class, LoginedValidator.instance);
    }
}
