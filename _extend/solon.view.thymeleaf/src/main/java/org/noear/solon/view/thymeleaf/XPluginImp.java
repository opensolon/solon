package org.noear.solon.view.thymeleaf;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;
import org.thymeleaf.processor.element.IElementTagProcessor;

public class XPluginImp implements XPlugin {
    public static boolean output_meta = false;

    @Override
    public void start(XApp app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        ThymeleafRender render = ThymeleafRender.global();

        Aop.context().beanOnloaded(() -> {
            Aop.context().beanForeach((k, v) -> {
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

        XBridge.renderRegister(render);
        XBridge.renderMapping(".html",render);
    }
}
