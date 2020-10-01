package org.noear.solon.boot.jdksocket;

import org.noear.solonx.socket.api.XSocketMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private ServerSocket server;
    private SocketProtocol protocol;
    private ExecutorService pool = Executors.newCachedThreadPool();

    private SocketProcessor processor = new SocketProcessor();

    public void setProtocol(SocketProtocol protocol) {
        this.protocol = protocol;
    }

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
        server = new ServerSocket(port);

        System.out.println("Server started, waiting for customer connection...");

        while (true) {
            Socket connector = server.accept();
            SocketSession session = processor.onOpen(connector);

            pool.execute(() -> {
                while (true) {
                    if (session.isOpen() == false) {
                        processor.onClosed(connector);
                        return;
                    }

                    XSocketMessage message = session.receive(protocol);
                    if (message != null) {
                        pool.execute(() -> {
                            processor.onMessage(session, message);
                        });
                    }

                }
            });
        }
    }

    public void stop() {
        if (server.isClosed()) {
            return;
        }

        try {
            server.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
