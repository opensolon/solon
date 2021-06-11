package org.noear.solon.boot.socketd.jdksocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.SessionManager;

public class XPluginImp implements Plugin {
    protected static Signal _signal;

    BioServer _server;

    public static String solon_boot_ver(){
        return "jdksocket-socketd/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());

        if (app.enableSocketD() == false) {
            return;
        }

        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: java.net.ServerSocket(jdksocket-socketd)");

        String _name = app.cfg().get("server.socket.name");
        int _port = app.cfg().getInt("server.socket.port", 0);
        if (_port < 1) {
            _port = 20000 + app.port();
        }

        try {
            _server = new BioServer();
            _server.start(_port);

            _signal = new SignalSim(_name, _port, "tcp", SignalType.SOCKET);
            app.signalAdd(_signal);

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: jdksocket-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            PrintUtil.info("Server:main: jdksocket-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if(_server == null){
            return;
        }

        _server.stop();
        _server = null;
        PrintUtil.info("Server:main: jdksocket-socketd: Has Stopped " + solon_boot_ver());
    }
}
