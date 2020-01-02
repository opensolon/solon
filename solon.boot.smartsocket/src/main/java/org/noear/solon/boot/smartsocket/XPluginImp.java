package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.noear.solon.core.XPlugin;
import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements XPlugin {

    private AioQuickServer<SocketMessage> server = null;

    @Override
    public void start(XApp app) {
        if(app.enableSocket == false){
            return;
        }

        int _port = 20000 + app.port();

        SsContextHandler contextHandler = new SsContextHandler(app);

        SsProtocol protocol = new SsProtocol();
        SsProcessor processor = new SsProcessor(contextHandler);

        server = new AioQuickServer<>(_port, protocol, processor);
        server.setBannerEnabled(false);

        try {
            server.start();
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
