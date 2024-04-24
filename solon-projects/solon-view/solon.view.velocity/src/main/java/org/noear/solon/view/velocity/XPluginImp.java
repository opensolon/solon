package org.noear.solon.view.velocity;

import org.apache.velocity.runtime.directive.Directive;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.view.velocity.tags.AuthPermissionsTag;
import org.noear.solon.view.velocity.tags.AuthRolesTag;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        VelocityRender render = VelocityRender.global();

        context.lifecycle(LifecycleIndex.plugin_bean_uses, () -> {
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
        context.wrapAndPut(VelocityRender.class, render);

        if (ClassUtil.hasClass(() -> AuthUtil.class)) {
            render.putDirective(new AuthPermissionsTag());
            render.putDirective(new AuthRolesTag());
        }
    }
}
