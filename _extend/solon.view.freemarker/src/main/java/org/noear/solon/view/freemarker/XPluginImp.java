package org.noear.solon.view.freemarker;

import freemarker.template.TemplateDirectiveModel;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.auth.tags.AuthConstants;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.i18n.tags.I18nConstants;
import org.noear.solon.view.freemarker.tags.AuthPermissionsTag;
import org.noear.solon.view.freemarker.tags.AuthRolesTag;
import org.noear.solon.view.freemarker.tags.I18nTag;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        FreemarkerRender render = FreemarkerRender.global();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
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

        if (Utils.loadClass("org.noear.solon.auth.AuthUtil") != null) {
            render.putDirective(AuthConstants.TAG_authPermissions, new AuthPermissionsTag());
            render.putDirective(AuthConstants.TAG_authRoles, new AuthRolesTag());
        }

        if (Utils.loadClass("org.noear.solon.i18n.I18nUtil") != null) {
            render.putDirective(I18nConstants.TAG_i18n, new I18nTag());
        }
    }
}
