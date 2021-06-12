package org.noear.solon.view.velocity;

import org.apache.velocity.runtime.directive.Directive;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.auth.tags.Constants;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.view.velocity.tags.AuthPermissionsTag;
import org.noear.solon.view.velocity.tags.AuthRolesTag;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        VelocityRender render = VelocityRender.global();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (v.raw() instanceof Directive) {
                        render.putDirective(v.name(), v.clz());
                    }
                    return;
                }

                if (k.startsWith("share:")) { //java share object
                    render.putVariable(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        RenderManager.register(render);
        RenderManager.mapping(".vm", render);

        if (Utils.loadClass("org.noear.solon.auth.AuthUtil") != null) {
            render.putDirective(Constants.TAG_authPermissions, AuthPermissionsTag.class);
            render.putDirective(Constants.TAG_authRoles, AuthRolesTag.class);
        }
    }
}
