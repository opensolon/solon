package org.noear.solon.view.enjoy;

import com.jfinal.template.Directive;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.auth.tags.AuthConstants;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.view.enjoy.tags.AuthPermissionsTag;
import org.noear.solon.view.enjoy.tags.AuthRolesTag;

@SuppressWarnings("unchecked")
public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;

        EnjoyRender render = EnjoyRender.global();

        context.beanOnloaded((ctx) -> {
            ctx.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (Directive.class.isAssignableFrom(v.clz())) {
                        render.putDirective(k.split(":")[1], (Class<? extends Directive>) v.clz());
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
        RenderManager.mapping(".shtm", render);

        if (Utils.loadClass("org.noear.solon.auth.AuthUtil") != null) {
            render.putDirective(AuthConstants.TAG_authPermissions, AuthPermissionsTag.class);
            render.putDirective(AuthConstants.TAG_authRoles, AuthRolesTag.class);
        }
    }
}
