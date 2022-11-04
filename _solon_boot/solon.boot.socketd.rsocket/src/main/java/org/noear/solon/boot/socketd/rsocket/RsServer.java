package org.noear.solon.boot.socketd.rsocket;


import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.socketd.client.rsocket.RsAcceptor;

import java.io.IOException;

class RsServer implements ServerLifecycle {
    private CloseableChannel server;

    private void start0(String host, int port) throws IOException {
        TcpServerTransport transport = null;

        if (Utils.isEmpty(host)) {
            transport = TcpServerTransport.create("localhost", port);
        } else {
            transport = TcpServerTransport.create(host, port);
        }

        server = RSocketServer
                .create(new RsAcceptor())
                .bind(transport)
                .block();

        LogUtil.info("Server started, waiting for customer connection...");
    }

    @Override
    public void start(String host, int port) throws Throwable {
        new Thread(() -> {
            try {
                start0(host, port);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).start();
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
