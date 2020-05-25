package org.noear.solon.boot.fineio;

import org.noear.fineio.NetServer;
import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {


    public static String solon_boot_ver() {
        return "jdk socket nio jdk8/1.0.5.10";
    }

    NetServer<SocketMessage> _server;

    @Override
    public void start(XApp app) {
        long time_start = System.currentTimeMillis();

        SocketMessageProcessor processor = new SocketMessageProcessor();
        SocketMessageProtocol protocol = new SocketMessageProtocol();

        int _port = 20000 + app.port();

        try {
            _server = NetServer.nio(protocol, processor);

            _server.start(_port);

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: Started ServerConnector@{[Socket nio]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop();
        _server = null;
        System.out.println("solon.Server:main: Has Stopped " + solon_boot_ver());
    }
}
