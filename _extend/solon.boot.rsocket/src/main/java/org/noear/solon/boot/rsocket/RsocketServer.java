package org.noear.solon.boot.rsocket;


import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.RSocketProxy;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RsocketServer {
    private CloseableChannel server;
    private ExecutorService pool = Executors.newCachedThreadPool();

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
        server = RSocketServer.create((setup, sendingSocket) -> Mono.just(new RSocketProxy(new RsocketHandler())))
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
