package org.noear.solon.boot.websocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.WebSocketServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.socketd.SessionManager;

import java.net.Inet4Address;

public class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private WsServer _server = null;

    public static String solon_boot_ver() {
        return "org.java_websocket 1.5.0/" + Solon.cfg().version();
    }

    @Override
    public void start(AopContext context) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());

        if (Solon.app().enableWebSocket() == false) {
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

    private void start0(SolonApp app) throws Throwable{
        //初始化属性
        ServerProps.init();

        WebSocketServerProps props = new WebSocketServerProps(10000);
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();

        long time_start = System.currentTimeMillis();


        LogUtil.global().info("Server:main: org.java_websocket 1.5.0(websocket)");


        if (Utils.isEmpty(_host)) {
            _server = new WsServer(_port);
        } else {
            _server = new WsServer(Inet4Address.getByName(_host), _port);
        }

        _server.start();

        _signal = new SignalSim(_name, _host, _port, "ws", SignalType.WEBSOCKET);

        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        LogUtil.global().info("Connector:main: websocket: Started ServerConnector@{HTTP/1.1,[WebSocket]}{0.0.0.0:" + _port + "}");
        LogUtil.global().info("Server:main: websocket: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            LogUtil.global().info("Server:main: websocket: Has Stopped " + solon_boot_ver());
        }
    }
}
