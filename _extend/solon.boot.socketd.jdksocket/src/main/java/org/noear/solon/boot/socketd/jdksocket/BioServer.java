package org.noear.solon.boot.socketd.jdksocket;

import org.noear.solon.Solon;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.NamedThreadFactory;
import org.noear.solon.socketd.client.jdksocket.BioReceiver;
import org.noear.solon.socketd.client.jdksocket.BioSocketSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BioServer {
    private ServerSocket server;
    private ExecutorService pool = Executors.newCachedThreadPool(new NamedThreadFactory("jdksocket-"));

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

        PrintUtil.info("Server started, waiting for customer connection...");

        while (true) {
            Socket socket = server.accept();

            Session session = BioSocketSession.get(socket);
            Solon.app().listener().onOpen(session);

            pool.execute(() -> {
                while (true) {
                    if (socket.isClosed()) {
                        Solon.app().listener().onClose(session);
                        BioSocketSession.remove(socket);
                        break;
                    }

                    try {
                        Message message = BioReceiver.receive(socket);

                        if (message != null) {
                            pool.execute(() -> {
                                try {
                                    Solon.app().listener().onMessage(session, message);
                                } catch (Throwable ex) {
                                    Solon.app().listener().onError(session, ex);
                                }
                            });
                        }
                    } catch (Throwable ex) {
                        Solon.app().listener().onError(session, ex);
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
