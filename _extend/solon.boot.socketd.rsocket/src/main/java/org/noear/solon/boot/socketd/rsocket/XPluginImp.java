package org.noear.solon.boot.socketd.rsocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.socketd.SessionManager;

public class XPluginImp implements Plugin {
    RsServer _server;

    public static String solon_boot_ver(){
        return "rsocket-socketd/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());

        if(app.enableSocketD() == false){
            return;
        }

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: java.net.ServerSocket(rsocket-socketd)");

        int _port = 20000 + app.port();

        try {
            _server = new RsServer();

            _server.start(_port);

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: rsocket-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: rsocket-socketd: Started @" + (time_end - time_start) + "ms");
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
        System.out.println("solon.Server:main: rsocket-socketd: Has Stopped " + solon_boot_ver());
    }
}
