package org.noear.solon.view.fastjson;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        app.renderSet(new FastjsonRender());
    }
}
