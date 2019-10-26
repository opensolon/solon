package org.noear.solon.serialization.jackson;

import org.noear.solon.XApp;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        JacksonRender render = new JacksonRender();

        //XRenderManager.register(render);
        XRenderManager.mapping(XRenderManager.mapping_model,render);
    }
}
