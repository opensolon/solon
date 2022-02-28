package org.noear.solon.boot.socketd.smartsocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.event.EventBus;

import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.SessionManager;
import org.noear.solon.socketd.SocketProps;
import org.noear.solon.socketd.client.smartsocket.AioProtocol;
import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements Plugin {
    private static Signal _signal;
    public static Signal signal(){
        return _signal;
    }

    private AioQuickServer _server = null;

    public static String solon_boot_ver() {
        return "smartsocket-socketd 1.5/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());


        if (app.enableSocketD() == false) {
            return;
        }

        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: SmartSocket 1.5(smartsocket-socketd)");


        String _name = app.cfg().get(ServerConstants.SERVER_SOCKET_NAME);
        int _port = app.cfg().getInt(ServerConstants.SERVER_SOCKET_PORT, 0);
        if (_port < 1) {
            _port = 20000 + app.port();
        }

        try {
            _server = new AioQuickServer(_port, AioProtocol.instance, new AioServerProcessor());
            _server.setBannerEnabled(false);
            if (SocketProps.readBufferSize() > 0) {
                _server.setReadBufferSize(SocketProps.readBufferSize());
            }
            if (SocketProps.writeBufferSize() > 0) {
                _server.setWriteBuffer(SocketProps.writeBufferSize(), 16);
            }
            _server.start();

            _signal = new SignalSim(_name, _port, "tcp", SignalType.SOCKET);
            app.signalAdd(_signal);

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: smartsocket-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            PrintUtil.info("Server:main: smartsocket-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.shutdown();
            _server = null;

            PrintUtil.info("Server:main: smartsocket-socketd: Has Stopped " + solon_boot_ver());
        }
    }
}
