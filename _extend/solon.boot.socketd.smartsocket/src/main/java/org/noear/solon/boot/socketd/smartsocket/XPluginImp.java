package org.noear.solon.boot.socketd.smartsocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.event.EventBus;

import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.SessionManager;
import org.noear.solon.socketd.SocketProps;
import org.noear.solon.socketd.client.smartsocket.AioProtocol;
import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements Plugin {
    private AioQuickServer server = null;

    public static String solon_boot_ver() {
        return "smartsocket-socketd 1.5.4/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());


        if (app.enableSocketD() == false) {
            return;
        }

        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: SmartSocket 1.5.4(smartsocket-socketd)");


        String _name = app.cfg().get("server.socket.name");
        int _port = app.cfg().getInt("server.socket.port", 0);
        if (_port < 1) {
            _port = 20000 + app.port();
        }

        try {
            server = new AioQuickServer(_port, AioProtocol.instance, new AioServerProcessor());
            server.setBannerEnabled(false);
            if (SocketProps.readBufferSize() > 0) {
                server.setReadBufferSize(SocketProps.readBufferSize());
            }
            if (SocketProps.writeBufferSize() > 0) {
                server.setWriteBuffer(SocketProps.writeBufferSize(), 16);
            }
            server.start();


            app.signalAdd(new SignalSim(_name, _port, "tcp", SignalType.SOCKET));

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: smartsocket-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            PrintUtil.info("Server:main: smartsocket-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;

            PrintUtil.info("Server:main: smartsocket-socketd: Has Stopped " + solon_boot_ver());
        }
    }
}
