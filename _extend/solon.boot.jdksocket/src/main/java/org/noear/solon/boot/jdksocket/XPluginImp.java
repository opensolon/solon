package org.noear.solon.boot.jdksocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    SocketServer _server;

    public static String solon_boot_ver(){
        return "jdk socket jdk8/1.0.22";
    }

    @Override
    public void start(XApp app) {

        long time_start = System.currentTimeMillis();

        SocketProtocol protocol = new SocketProtocol();
        SocketContextHandler handler = new SocketContextHandler();

        System.out.println("solon.Server:main: java.net.ServerSocket jdk8");

        int _port = 20000 + app.port();

        try {
            _server = new SocketServer();
            _server.setProtocol(protocol);
            _server.setHandler(handler);

            _server.start(_port);

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if(_server == null){
            return;
        }

        _server.stop();
        _server = null;
        System.out.println("solon.Server:main: Has Stopped " + solon_boot_ver());
    }
}
