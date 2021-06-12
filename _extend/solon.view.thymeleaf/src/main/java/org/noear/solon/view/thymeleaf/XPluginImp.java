package org.noear.solon.view.thymeleaf;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.auth.tags.Constants;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.view.thymeleaf.tags.AuthDialect;
import org.noear.solon.view.thymeleaf.tags.AuthPermissionsTag;
import org.noear.solon.view.thymeleaf.tags.AuthRolesTag;
import org.thymeleaf.processor.element.IElementTagProcessor;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        ThymeleafRender render = ThymeleafRender.global();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if(IElementTagProcessor.class.isAssignableFrom(v.clz())) {
                        render.putDirective(k.split(":")[1], v.raw());
                    }
                    return;
                }

                if(k.startsWith("share:")){ //java share object
                    render.putVariable(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        RenderManager.register(render);
        RenderManager.mapping(".html",render);

        if (Utils.loadClass("org.noear.solon.auth.AuthUtil") != null) {
            AuthDialect.global().addProcessor(new AuthPermissionsTag());
            AuthDialect.global().addProcessor(new AuthRolesTag());
        }
    }
}
