package org.noear.solon.boot.socketd.jdksocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.SocketSignalProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.socketd.SessionManager;

public class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    BioServer _server;

    public static String solon_boot_ver() {
        return "jdksocket-socketd/" + Solon.cfg().version();
    }

    @Override
    public void start(AopContext context) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());

        if (Solon.app().enableSocketD() == false) {
            return;
        }

        context.beanOnloaded((ctx) -> {
            try {
                start0(Solon.app());
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        long time_start = System.currentTimeMillis();

        LogUtil.global().info("Server:main: java.net.ServerSocket(jdksocket-socketd)");


        SocketSignalProps props = new SocketSignalProps(20000);
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();


        _server = new BioServer();
        _server.start(_host, _port);

        _signal = new SignalSim(_name, _host, _port, "tcp", SignalType.SOCKET);
        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        LogUtil.global().info("Connector:main: jdksocket-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
        LogUtil.global().info("Server:main: jdksocket-socketd: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop();
        _server = null;
        LogUtil.global().info("Server:main: jdksocket-socketd: Has Stopped " + solon_boot_ver());
    }
}
