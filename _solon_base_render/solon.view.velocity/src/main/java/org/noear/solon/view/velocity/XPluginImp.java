package org.noear.solon.view.velocity;

import org.apache.velocity.runtime.directive.Directive;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.view.velocity.tags.AuthPermissionsTag;
import org.noear.solon.view.velocity.tags.AuthRolesTag;

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        VelocityRender render = VelocityRender.global();

        context.lifecycle(-99, () -> {
            context.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (v.raw() instanceof Directive) {
                        render.putDirective(v.raw());
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

        if (ClassUtil.hasClass(() -> AuthUtil.class)) {
            render.putDirective(new AuthPermissionsTag());
            render.putDirective(new AuthRolesTag());
        }
    }
}
