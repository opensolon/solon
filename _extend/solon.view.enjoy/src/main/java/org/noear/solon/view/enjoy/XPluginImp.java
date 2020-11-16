package org.noear.solon.view.enjoy;

import com.jfinal.template.Directive;
import org.noear.solon.Solon;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

@SuppressWarnings("unchecked")
public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(Solon app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        EnjoyRender render =  EnjoyRender.global();

        Aop.context().beanOnloaded(() -> {
            Aop.context().beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if(Directive.class.isAssignableFrom(v.clz())){
                        render.addDirective(k.split(":")[1], (Class<? extends Directive>)v.clz());
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
        Bridge.renderMapping(".shtm",render);
    }
}
