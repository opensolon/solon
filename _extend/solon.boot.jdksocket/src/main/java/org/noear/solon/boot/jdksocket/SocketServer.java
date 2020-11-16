package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.extend.xsocket.MessageListenerProxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private ServerSocket server;
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
        server = new ServerSocket(port);

        System.out.println("Server started, waiting for customer connection...");

        while (true) {
            Socket socket = server.accept();

            MessageSession session = _SocketSession.get(socket);
            MessageListenerProxy.getGlobal().onOpen(session);

            pool.execute(() -> {
                while (true) {
                    if (socket.isClosed()) {
                        MessageListenerProxy.getGlobal().onClose(session);
                        break;
                    }

                    Message message = _SocketSession.receive(socket);
                    if (message != null) {

                        pool.execute(() -> {
                            try {
                                MessageListenerProxy.getGlobal().onMessage(session, message, false);
                            } catch (Throwable ex) {
                                MessageListenerProxy.getGlobal().onError(session, ex);
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
