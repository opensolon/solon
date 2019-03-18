package org.noear.solon.view.enjoy;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        EnjoyRender render = new EnjoyRender();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("tag:")) {
                    render.setSharedVariable(k.split(":")[1], v.raw());
                }
            });
        });

        app.renderSet(render);
    }
}
