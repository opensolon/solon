package org.noear.solon.boot.rsocket;


import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;

import java.io.IOException;

class RsocketServer {
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
                .create(RsocketAcceptor.instance)
                .bind(TcpServerTransport.create("localhost", port))
                .block();

        System.out.println("Server started, waiting for customer connection...");

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
