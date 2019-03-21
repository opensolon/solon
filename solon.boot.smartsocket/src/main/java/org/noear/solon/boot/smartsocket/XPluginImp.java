package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements XPlugin {

    private AioQuickServer<byte[]> server;

    @Override
    public  void start(XApp app) {
        int _port = 10000 + app.port();

        SsContextHandler contextHandler = new SsContextHandler(true, app);

        SsProtocol protocol = new SsProtocol();
        SsProcessor processor = new SsProcessor(contextHandler);

       server = new AioQuickServer<>(_port, protocol, processor);

        //server.setThreadNum(app.prop().rpcxMaxPoolSize());

        try {
            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
