package org.noear.solon.extend.satoken;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.auth.validator.RolesInterceptor;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanAroundAdd(SaCheckPermission.class, new RolesInterceptor());
    }
}
