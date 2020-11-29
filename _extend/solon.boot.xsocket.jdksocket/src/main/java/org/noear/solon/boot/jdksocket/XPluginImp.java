package org.noear.solon.boot.jdksocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.xsocket.SessionFactory;

public class XPluginImp implements Plugin {
    BioServer _server;

    public static String solon_boot_ver(){
        return "jdksocket-xsocket/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话工厂
        SessionFactory.setInstance(new _SessionFactoryImpl());

        if (app.enableSocket() == false) {
            return;
        }

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: java.net.ServerSocket(jdksocket-xsocket)");

        int _port = app.cfg().getInt("server.socket.port", 0);
        if (_port < 1) {
            _port = 20000 + app.port();
        }

        try {
            _server = new BioServer();
            _server.start(_port);

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: jdksocket-xsocket: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: jdksocket-xsocket: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
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
        System.out.println("solon.Server:main: jdksocket-xsocket: Has Stopped " + solon_boot_ver());
    }
}
