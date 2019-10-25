package org.noear.solon.view.enjoy;

import com.jfinal.template.Directive;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

@SuppressWarnings("unchecked")
public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        EnjoyRender render = new EnjoyRender();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("em:")) {
                    if(Directive.class.isAssignableFrom(v.clz())){
                        render.addDirective(k.split(":")[1], (Class<? extends Directive>)v.clz());
                    }else{
                        render.setSharedVariable(k.split(":")[1], v.raw());
                    }

                }
            });
        });

        XRenderManager.register(render);
    }
}
