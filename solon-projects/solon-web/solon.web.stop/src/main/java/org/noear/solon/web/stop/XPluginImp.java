package org.noear.solon.web.stop;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        boolean enable = context.cfg().getBool("solon.stop.enable", false);
        String path = context.cfg().get("solon.stop.path", "/_run/stop/");
        String whitelist = context.cfg().get("solon.stop.whitelist", "127.0.0.1");

        //单位为秒
        int delay = context.cfg().getInt("solon.stop.delay", 10);

        //开启WEB关闭
        if (enable) {
            Solon.app().get(path, (c) -> {
                int delay2 = c.paramAsInt("delay", delay);

                if (delay2 < 0) {
                    delay2 = 0;
                }

                if ("*".equals(whitelist)) {
                    Solon.stop(delay2);
                } else if (whitelist.equals(c.realIp())) {
                    Solon.stop(delay2);
                }
            });
        }
    }
}
