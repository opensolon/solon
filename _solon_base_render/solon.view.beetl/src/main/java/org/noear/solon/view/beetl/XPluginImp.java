package org.noear.solon.view.beetl;

import org.beetl.core.tag.Tag;
import org.noear.solon.Solon;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.tags.AuthConstants;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.view.beetl.tags.AuthPermissionsTag;
import org.noear.solon.view.beetl.tags.AuthRolesTag;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {

        BeetlRender render = BeetlRender.global();

        context.lifecycle(-99, () -> {
            context.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (Tag.class.isAssignableFrom(v.clz())) {
                        render.putDirective(k.split(":")[1], (Class<? extends Tag>) v.clz());
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
        RenderManager.mapping(".htm", render);
        RenderManager.mapping(".btl", render);

        if (ClassUtil.hasClass(() -> AuthUtil.class)) {
            render.putDirective(AuthConstants.TAG_authPermissions, AuthPermissionsTag.class);
            render.putDirective(AuthConstants.TAG_authRoles, AuthRolesTag.class);
        }
    }
}
