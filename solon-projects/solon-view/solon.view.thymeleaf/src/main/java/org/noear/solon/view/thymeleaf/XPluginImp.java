package org.noear.solon.view.thymeleaf;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.view.thymeleaf.tags.AuthDialect;
import org.noear.solon.view.thymeleaf.tags.AuthPermissionsTag;
import org.noear.solon.view.thymeleaf.tags.AuthRolesTag;
import org.thymeleaf.dialect.IDialect;

public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        ThymeleafRender render = ThymeleafRender.global();

        context.lifecycle(LifecycleIndex.plugin_bean_uses, () -> {
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
        context.wrapAndPut(ThymeleafRender.class, render);

        if (ClassUtil.hasClass(() -> AuthUtil.class)) {
            AuthDialect authDialect = new AuthDialect();
            authDialect.addProcessor(new AuthPermissionsTag(authDialect.getPrefix()));
            authDialect.addProcessor(new AuthRolesTag(authDialect.getPrefix()));
            render.putDirective(authDialect);
        }

        //添加 StandardLinkBuilder
        String baseUrl = Solon.cfg().serverContextPath();
        if (Utils.isEmpty(baseUrl)) {
            baseUrl = "";
        } else {
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
        }

        BaseUrlLinkBuilder baseUrlLinkBuilder = new BaseUrlLinkBuilder();
        baseUrlLinkBuilder.setBaseUrl(baseUrl);
        render.getProvider().setLinkBuilder(baseUrlLinkBuilder);
    }
}
