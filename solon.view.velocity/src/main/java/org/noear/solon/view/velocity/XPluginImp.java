package org.noear.solon.view.velocity;

import org.apache.velocity.runtime.directive.Directive;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        VelocityRender render = new VelocityRender();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("vm:")) {
                    if (v.raw() instanceof Directive) {
                        render.loadDirective(v.raw());
                    } else {
                        render.setSharedVariable(k.split(":")[1], v.raw());
                    }
                }
            });
        });

        XRenderManager.register(render);
        XRenderManager.mapping(".vm",render);
    }
}
