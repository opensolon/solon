package org.noear.solon.view.beetl;

import org.beetl.core.tag.Tag;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    public static boolean output_meta = false;

    @Override
    public void start(XApp app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        BeetlRender render =  BeetlRender.global();

        Aop.context().beanOnloaded(() -> {
            Aop.context().beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if(Tag.class.isAssignableFrom(v.clz())) {
                        render.registerTag(k.split(":")[1], v.clz());
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
        XBridge.renderMapping(".htm",render);
    }
}
