package org.noear.solon.view.thymeleaf;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;
import org.thymeleaf.processor.element.IElementTagProcessor;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        ThymeleafRender render = ThymeleafRender.global();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if(IElementTagProcessor.class.isAssignableFrom(v.clz())) {
                        render.setSharedVariable(k.split(":")[1], v.raw());
                    }
                    return;
                }

                if(k.startsWith("share:")){ //java share object
                    render.setSharedVariable(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        XRenderManager.register(render);
        XRenderManager.mapping(".html",render);
    }
}
