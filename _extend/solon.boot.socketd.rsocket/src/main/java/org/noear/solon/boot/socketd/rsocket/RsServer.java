package org.noear.solon.boot.socketd.rsocket;


import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.client.rsocket.RsAcceptor;

import java.io.IOException;

class RsServer {
    private CloseableChannel server;

    public void start(int port) {
        new Thread(() -> {
            try {
                start0(port);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).start();
    }

    private void start0(int port) throws IOException {
        server = RSocketServer
                .create(new RsAcceptor())
                .bind(TcpServerTransport.create("localhost", port))
                .block();

        PrintUtil.info("Server started, waiting for customer connection...");

    }

    public void stop() {
        if (server == null || server.isDisposed()) {
            return;
        }

        try {
            server.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
