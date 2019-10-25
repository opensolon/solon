package org.noear.solon.serialization.snack3;

import org.noear.solon.XApp;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        SnackRender render = new SnackRender();

        //XRenderManager.register(render);
        XRenderManager.mapping(XRenderManager.mapping_model,render);
    }
}
