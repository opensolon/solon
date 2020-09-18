package org.noear.solon.boot.nettyhttp;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    HttpServer _server;

    public static String solon_boot_ver(){
        return "netty http 4.1/1.0.32";
    }

    @Override
    public void start(XApp app) {
        if(app.enableHttp == false){
            return;
        }

        XServerProp.init();

        long time_start = System.currentTimeMillis();

        try {
            _server = new HttpServer(app.port());

            System.out.println("solon.Server:main: NettyHttpServer 4.x");

            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            System.out.println("solon.Server:main: Has Stopped " + solon_boot_ver());
        }
    }
}
