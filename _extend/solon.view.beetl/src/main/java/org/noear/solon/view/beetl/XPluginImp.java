package org.noear.solon.view.beetl;

import org.beetl.core.tag.Tag;
import org.noear.solon.Solon;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(Solon app) {
        output_meta = app.props().getInt("solon.output.meta", 0) > 0;

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

        Bridge.renderRegister(render);
        Bridge.renderMapping(".htm",render);
        Bridge.renderMapping(".btl",render);
    }
}
