package org.noear.solon.boot.smarthttp;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.smartboot.http.HttpBootstrap;

public final class XPluginImp implements XPlugin {

    HttpBootstrap _server = null;

    public static String solon_boot_ver(){
        return "smart http 1.0.15/1.0.15";
    }

    @Override
    public void start(XApp app) {
        if(app.enableHttp == false){
            return;
        }

        XServerProp.init();

        long time_start = System.currentTimeMillis();

        SmartHttpContextHandler _handler = new SmartHttpContextHandler();

        _server = new HttpBootstrap();
        _server.pipeline().next(_handler);


        System.out.println("solon.Server:main: SmartHttpServer 1.0.15");

        try {

            _server.setThreadNum(Runtime.getRuntime().availableProcessors() + 2)
                    .setPort(app.port())
                    .start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.shutdown();
            _server = null;

            System.out.println("solon.Server:main: Has Stopped " + solon_boot_ver());
        }
    }
}

