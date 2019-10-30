package org.noear.solon.boot.nettyhttp;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    HttpServer _server;
    @Override
    public void start(XApp app) {
        long time_start = System.currentTimeMillis();



        try {
            String tmp = Aop.prop().get("solon.boot.netty.maxContentLength","").trim();
            if(tmp.endsWith("m")) {
                try {
                    HttpServerConfig.maxContentLength = Integer.parseInt(tmp.substring(0, tmp.length() - 1)) * 1204;
                } catch (Exception ex) {
                    throw new RuntimeException("Config error: solon.boot.netty.maxContentLength:" + tmp);
                }
            }

            _server = new HttpServer(app.port());

            System.out.println("oejs.Server:main: NettyHttpServer 4.x");

            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("oejs.AbstractConnector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
