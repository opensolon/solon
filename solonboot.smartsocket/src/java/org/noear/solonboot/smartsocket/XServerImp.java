package org.noear.solonboot.smartsocket;

import org.noear.solonboot.XApp;
import org.noear.solonboot.protocol.XServer;
import org.smartboot.socket.transport.AioQuickServer;

public final class XServerImp implements XServer {

    @Override
    public  void start(XApp app) {
        int _port = 10000 + app.port();

        SsContextHandler contextHandler = new SsContextHandler(true, app);

        SsProtocol protocol = new SsProtocol();
        SsProcessor processor = new SsProcessor(contextHandler);

        AioQuickServer<byte[]> server = new AioQuickServer<>(_port, protocol, processor);

        //server.setThreadNum(app.prop().rpcxMaxPoolSize());

        try {
            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
