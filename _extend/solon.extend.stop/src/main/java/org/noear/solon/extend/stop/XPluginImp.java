package org.noear.solon.extend.stop;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        boolean enabled = app.prop().getBool("solon.stop.enabled", false);
        String path = app.prop().get("solon.stop.path", "/run/stop/");
        String host = app.prop().get("solon.stop.host", "127.0.0.1");

        long delay = XApp.cfg().getLong("solon.stop.delay", 0);


        if (enabled) {
            app.get(path, (c) -> {
                long delay2 = c.paramAsLong("delay", delay);

                if(delay2 < 0){
                    delay2 = 0;
                }

                if ("*".equals(host)) {
                    XApp.stop(true, delay2);
                } else if (host.equals(c.uri().getHost())) {
                    XApp.stop(true, delay2);
                }
            });
        }
    }
}
