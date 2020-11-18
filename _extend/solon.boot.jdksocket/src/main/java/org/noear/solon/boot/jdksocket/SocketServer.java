package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.ListenerProxy;

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

            Session session = _SocketSession.get(socket);
            ListenerProxy.getGlobal().onOpen(session);

            pool.execute(() -> {
                while (true) {
                    if (socket.isClosed()) {
                        ListenerProxy.getGlobal().onClose(session);
                        _SocketSession.remove(socket);
                        break;
                    }

                    Message message = _SocketSession.receive(socket);
                    if (message != null) {

                        pool.execute(() -> {
                            try {
                                ListenerProxy.getGlobal().onMessage(session, message, false);
                            } catch (Throwable ex) {
                                ListenerProxy.getGlobal().onError(session, ex);
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
