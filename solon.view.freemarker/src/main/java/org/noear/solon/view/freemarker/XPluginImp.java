package org.noear.solon.view.freemarker;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        FreemarkerRender render = new FreemarkerRender();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("ftl:")) {
                    render.setSharedVariable(k.split(":")[1], v.raw());
                }
            });
        });

        XRenderManager.register(render);
        XRenderManager.mapping(".ftl",render);
    }
}
