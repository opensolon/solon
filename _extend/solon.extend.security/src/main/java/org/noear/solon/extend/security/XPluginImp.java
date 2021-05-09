package org.noear.solon.extend.security;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.security.annotation.*;
import org.noear.solon.extend.security.validator.AuthGuestValidator;
import org.noear.solon.extend.security.validator.AuthPermissionsValidator;
import org.noear.solon.extend.security.validator.AuthRolesValidator;
import org.noear.solon.extend.security.validator.AuthUserValidator;
import org.noear.solon.extend.validation.ValidatorManager;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        ValidatorManager.global().register(AuthGuest.class, AuthGuestValidator.instance);
        ValidatorManager.global().register(AuthPermissions.class, AuthPermissionsValidator.instance);
        ValidatorManager.global().register(AuthRoles.class, AuthRolesValidator.instance);
        ValidatorManager.global().register(AuthUser.class, AuthUserValidator.instance);
    }
}
