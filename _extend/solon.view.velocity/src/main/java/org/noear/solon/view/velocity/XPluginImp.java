package org.noear.solon.view.velocity;

import org.apache.velocity.runtime.directive.Directive;
import org.noear.solon.Solon;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(Solon app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        VelocityRender render =  VelocityRender.global();

        Aop.context().beanOnloaded(() -> {
            Aop.context().beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (v.raw() instanceof Directive) {
                        render.loadDirective(v.raw());
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
        Bridge.renderMapping(".vm",render);
    }
}
