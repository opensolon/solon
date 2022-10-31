package org.noear.solon.view.thymeleaf;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.view.thymeleaf.tags.AuthDialect;
import org.noear.solon.view.thymeleaf.tags.AuthPermissionsTag;
import org.noear.solon.view.thymeleaf.tags.AuthRolesTag;
import org.thymeleaf.dialect.IDialect;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;

        ThymeleafRender render = ThymeleafRender.global();

        context.beanOnloaded((ctx) -> {
            ctx.beanForeach((k, v) -> {
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

        if (Utils.loadClass("org.noear.solon.auth.AuthUtil") != null) {
            AuthDialect authDialect = new AuthDialect();
            authDialect.addProcessor(new AuthPermissionsTag(authDialect.getPrefix()));
            authDialect.addProcessor(new AuthRolesTag(authDialect.getPrefix()));
            render.putDirective(authDialect);
        }
    }
}
