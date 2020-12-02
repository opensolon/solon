package org.noear.solon.boot.jdksocket;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ListenerProxy;

import java.net.Socket;
import java.net.SocketException;

class BioConnector {
    String host;
    int port;

    public BioConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Socket start() {
        try {
            Socket socket = new Socket(host, port);

            return socket;
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }

    public void startReceive(Session session, Socket socket) {
        Utils.pools.submit(() -> {
            while (true) {
                Message message = BioReceiver.receive(socket);

                if (message != null) {
                    try {
                        ListenerProxy.getGlobal().onMessage(session, message, false);
                    } catch (Throwable ex) {
                        EventBus.push(ex);
                    }
                } else {
                    break;
                }
            }
        });
    }
}
