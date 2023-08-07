package org.noear.solon.view.freemarker;

import freemarker.template.TemplateDirectiveModel;

import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.tags.AuthConstants;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.view.freemarker.tags.AuthPermissionsTag;
import org.noear.solon.view.freemarker.tags.AuthRolesTag;

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        FreemarkerRender render = FreemarkerRender.global();

        context.lifecycle(-99, () -> {
            context.beanForeach((k, v) -> {
                if (k.startsWith("view:") || k.startsWith("ftl:")) {
                    //java view widget
                    if (TemplateDirectiveModel.class.isAssignableFrom(v.clz())) {
                        render.putDirective(k.split(":")[1], v.raw());
                    }
                }

                if (k.startsWith("share:")) {
                    //java share object
                    render.putVariable(k.split(":")[1], v.raw());
                    return;
                }
            });
        });


        RenderManager.register(render);
        RenderManager.mapping(".ftl", render);

        if (ClassUtil.hasClass(() -> AuthUtil.class)) {
            render.putDirective(AuthConstants.TAG_authPermissions, new AuthPermissionsTag());
            render.putDirective(AuthConstants.TAG_authRoles, new AuthRolesTag());
        }
    }
}
