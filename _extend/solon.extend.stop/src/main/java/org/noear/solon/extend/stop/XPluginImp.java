package org.noear.solon.extend.stop;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        boolean enabled = app.cfg().getBool("solon.stop.enabled", false);
        String path = app.cfg().get("solon.stop.path", "/run/stop/");
        String host = app.cfg().get("solon.stop.host", "127.0.0.1");

        //单位为秒
        int delay = Solon.cfg().getInt("solon.stop.delay", 10);

        //开启WEB关闭
        if (enabled) {
            app.get(path, (c) -> {
                int delay2 = c.paramAsInt("delay", delay);

                if (delay2 < 0) {
                    delay2 = 0;
                }

                if ("*".equals(host)) {
                    Solon.stop(true, delay2);
                } else if (host.equals(c.uri().getHost())) {
                    Solon.stop(true, delay2);
                }
            });
        }
    }
}
