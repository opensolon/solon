package org.noear.solon.boot.smartsocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.message.Message;

import org.noear.solon.extend.socketd.SessionFactoryManager;
import org.noear.solon.extend.socketd.SessionManager;
import org.noear.solon.extend.socketd.SocketProps;
import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements Plugin {
    private AioQuickServer<Message> server = null;

    public static String solon_boot_ver() {
        return "smartsocket-socketd 1.5.0/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        //注册会话工厂
        SessionManager.register(new _SessionManagerImpl());
        SessionFactoryManager.register(new _SessionFactoryImpl());


        if (app.enableSocket() == false) {
            return;
        }

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: SmartSocket 1.5.0(smartsocket-socketd)");

        int _port = app.cfg().getInt("server.socket.port", 0);
        if (_port < 1) {
            _port = 20000 + app.port();
        }

        try {
            server = new AioQuickServer<>(_port, AioProtocol.instance, new AioServerProcessor());
            server.setBannerEnabled(false);
            if (SocketProps.readBufferSize() > 0) {
                server.setReadBufferSize(SocketProps.readBufferSize());
            }
            if (SocketProps.writeBufferSize() > 0) {
                server.setWriteBuffer(SocketProps.writeBufferSize(), 16);
            }
            server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: smartsocket-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: smartsocket-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;

            System.out.println("solon.Server:main: smartsocket-socketd: Has Stopped " + solon_boot_ver());
        }
    }
}
