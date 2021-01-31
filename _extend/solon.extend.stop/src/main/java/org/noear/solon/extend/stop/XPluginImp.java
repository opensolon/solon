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

        long delay = Solon.cfg().getLong("solon.stop.delay", 10000);


        //添加关闭勾子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> StopUtils.stop0(false, delay)));

        //开启WEB关闭
        if (enabled) {
            app.get(path, (c) -> {
                long delay2 = c.paramAsLong("delay", delay);

                if(delay2 < 0){
                    delay2 = 0;
                }

                if ("*".equals(host)) {
                    StopUtils.stop(true, delay2);
                } else if (host.equals(c.uri().getHost())) {
                    StopUtils.stop(true, delay2);
                }
            });
        }
    }
}
