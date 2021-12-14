package org.noear.solon.extend.stop;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        boolean enable = app.cfg().getBool("solon.stop.enable", false);
        String path = app.cfg().get("solon.stop.path", "/_run/stop/");
        String host = app.cfg().get("solon.stop.host", "127.0.0.1");

        //单位为秒
        int delay = Solon.cfg().getInt("solon.stop.delay", 10);

        //开启WEB关闭
        if (enable) {
            app.get(path, (c) -> {
                int delay2 = c.paramAsInt("delay", delay);

                if (delay2 < 0) {
                    delay2 = 0;
                }

                if ("*".equals(host)) {
                    Solon.stop(delay2);
                } else if (host.equals(c.realIp())) {
                    Solon.stop(delay2);
                }
            });
        }
    }
}
