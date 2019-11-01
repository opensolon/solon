package org.noear.solon.extend.stop;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        app.get("/_run/_stop/", (c)->{
            XApp.stop();
        });
    }
}
