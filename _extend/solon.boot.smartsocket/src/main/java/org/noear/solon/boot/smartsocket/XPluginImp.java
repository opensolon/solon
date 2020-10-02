package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonx.socket.api.XSocketMessage;

import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements XPlugin {

    private AioQuickServer<XSocketMessage> server = null;

    @Override
    public void start(XApp app) {
        if(app.enableSocket() == false){
            return;
        }

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: Smartboot - smartsocket");

        int _port = 20000 + app.port();

        AioProtocol protocol = new AioProtocol();
        AioProcessor processor = new AioProcessor();

        try {
            server = new AioQuickServer<>(_port, protocol, processor);
            server.setBannerEnabled(false);

            server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
