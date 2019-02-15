package org.noear.solon.extend.staticfiles;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if(XUtil.getResource("/static") != null) {
            app.plug(new XResourceHandlerPlugin("/static"));
        }
    }
}
