package org.noear.solon.boot.jdkudp;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    SocketServer _server;

    public static String solon_boot_ver(){
        return "jdk tpc socket/1.1";
    }

    @Override
    public void start(XApp app) {

        if(app.enableSocket() == false){
            return;
        }

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: java.net.ServerSocket(jdksocket)");

        int _port = 20000 + app.port();

        SocketProtocol protocol = new SocketProtocol();

        try {
            _server = new SocketServer();
            _server.setProtocol(protocol);

            _server.start(_port);

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: jdksocket: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: jdksocket: Started @" + (time_end - time_start) + "ms");
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
        System.out.println("solon.Server:main: jdksocket: Has Stopped " + solon_boot_ver());
    }
}
