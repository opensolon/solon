package org.noear.solon.boot.socketd.rsocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.boot.prop.SocketSignalProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.SessionManager;

public class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    RsServer _server;

    public static String solon_boot_ver() {
        return "rsocket-socketd/" + Solon.cfg().version();
    }

    @Override
    public void start(AopContext context) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());

        if (Solon.app().enableSocketD() == false) {
            return;
        }

        context.beanOnloaded((ctx) -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) {
        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: java.net.ServerSocket(rsocket-socketd)");

        SocketSignalProps props = new SocketSignalProps(20000);
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();

        try {
            _server = new RsServer();
            _server.start(_host, _port);

            _signal = new SignalSim(_name, _port, "tcp", SignalType.SOCKET);

            app.signalAdd(_signal);

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: rsocket-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            PrintUtil.info("Server:main: rsocket-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop();
        _server = null;
        PrintUtil.info("Server:main: rsocket-socketd: Has Stopped " + solon_boot_ver());
    }
}
