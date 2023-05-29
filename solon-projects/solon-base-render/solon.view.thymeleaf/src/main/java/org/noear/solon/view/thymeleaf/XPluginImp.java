package org.noear.solon.view.thymeleaf;

import org.noear.solon.Solon;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.view.thymeleaf.tags.AuthDialect;
import org.noear.solon.view.thymeleaf.tags.AuthPermissionsTag;
import org.noear.solon.view.thymeleaf.tags.AuthRolesTag;
import org.thymeleaf.dialect.IDialect;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        ThymeleafRender render = ThymeleafRender.global();

        context.lifecycle(-99, () -> {
            context.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (IDialect.class.isAssignableFrom(v.clz())) {
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
        RenderManager.mapping(".html", render);

        if (ClassUtil.hasClass(() -> AuthUtil.class)) {
            AuthDialect authDialect = new AuthDialect();
            authDialect.addProcessor(new AuthPermissionsTag(authDialect.getPrefix()));
            authDialect.addProcessor(new AuthRolesTag(authDialect.getPrefix()));
            render.putDirective(authDialect);
        }
    }
}
