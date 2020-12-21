package org.noear.solon.boot.jdksocket;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ConnectorSimple;
import org.noear.solon.extend.socketd.ListenerProxy;
import org.noear.solon.extend.socketd.SocketProps;

import java.net.Socket;
import java.net.URI;

class BioConnector extends ConnectorSimple<Socket> {
    public BioConnector(URI uri, boolean autoReconnect) {
        super(uri, autoReconnect);
    }

    @Override
    public Class<Socket> driveType() {
        return Socket.class;
    }

    @Override
    public Socket open(Session session) {
        try {
            Socket socket = new Socket(uri().getHost(), uri().getPort());

            if (SocketProps.socketTimeout() > 0) {
                socket.setSoTimeout(SocketProps.socketTimeout());
            }

            startReceive(session, socket);

            return socket;
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }

    void startReceive(Session session, Socket socket) {
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
