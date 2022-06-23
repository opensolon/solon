package org.noear.solon.boot.socketd.jdksocket;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.NamedThreadFactory;
import org.noear.solon.socketd.client.jdksocket.BioReceiver;
import org.noear.solon.socketd.client.jdksocket.BioSocketSession;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BioServer implements ServerLifecycle {
    private ServerSocket server;
    private ExecutorService pool = Executors.newCachedThreadPool(new NamedThreadFactory("jdksocket-"));

    private void start0(String host, int port) throws IOException {
        if (Utils.isEmpty(host)) {
            server = new ServerSocket(port);
        } else {
            server = new ServerSocket(port, 50, Inet4Address.getByName(host));
        }


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

    @Override
    public void stop() {
        if (server == null || server.isClosed()) {
            return;
        }

        try {
            server.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
