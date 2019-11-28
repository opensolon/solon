package org.noear.solon.view.freemarker;

import freemarker.template.TemplateDirectiveModel;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        FreemarkerRender render = FreemarkerRender.global();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("view:") || k.startsWith("ftl:")) { //java view widget
                    if(TemplateDirectiveModel.class.isAssignableFrom(v.clz())) {
                        render.setSharedVariable(k.split(":")[1], v.raw());
                    }
                }

                if(k.startsWith("share:")){ //java share object
                    render.setSharedVariable(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        XRenderManager.register(render);
        XRenderManager.mapping(".ftl", render);
    }
}
