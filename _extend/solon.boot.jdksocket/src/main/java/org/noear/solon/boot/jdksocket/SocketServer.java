package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.XMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private ServerSocket server;
    private SocketProtocol protocol;
    private ExecutorService pool = Executors.newCachedThreadPool();

    private SocketListenerImp processor = new SocketListenerImp();

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
            Socket socket = server.accept();

            processor.onOpen(socket);

            pool.execute(() -> {
                while (true) {
                    if (socket.isClosed()) {
                        processor.onClosed(socket);
                        break;
                    }

                    XMessage message = _SocketSession.receive(socket, protocol);
                    if (message != null) {
                        pool.execute(() -> {
                            try {
                                processor.onMessage(socket, message);
                            } catch (Throwable ex) {
                                processor.onError(socket, ex);
                            }
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
