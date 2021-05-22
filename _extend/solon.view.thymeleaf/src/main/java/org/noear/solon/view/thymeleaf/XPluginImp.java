package org.noear.solon.view.thymeleaf;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
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

        RenderManager.register(render);
        RenderManager.mapping(".html",render);
    }
}
