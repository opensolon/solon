package org.noear.solon.view.beetl;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        BeetlRender render = new BeetlRender();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("tag:")) {
                    render.registerTag(k.split(":")[1], v.clz());
                }
            });
        });

        app.renderSet(render);
    }
}
