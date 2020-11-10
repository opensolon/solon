package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.XListener;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;
import org.noear.solon.extend.xsocket.XListenerProxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private ServerSocket server;
    private SocketProtocol protocol;
    private ExecutorService pool = Executors.newCachedThreadPool();

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

            XSession session = _SocketSession.get(socket);
            XListenerProxy.getGlobal().onOpen(session);

            pool.execute(() -> {
                while (true) {
                    if (socket.isClosed()) {
                        XListenerProxy.getGlobal().onClose(session);
                        break;
                    }

                    XMessage message = _SocketSession.receive(socket, protocol);
                    if (message != null) {
                        pool.execute(() -> {
                            try {
                                XListenerProxy.getGlobal().onMessage(session, message, false);
                            } catch (Throwable ex) {
                                XListenerProxy.getGlobal().onError(session, ex);
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
