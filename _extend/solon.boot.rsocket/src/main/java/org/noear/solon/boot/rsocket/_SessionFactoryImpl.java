package org.noear.solon.boot.rsocket;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.Connector;
import org.noear.solon.extend.socketd.SessionFactory;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

/**
 * @author noear 2020/12/14 created
 */
public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"tcp"};
    }

    @Override
    public Session createSession(Connector connector) {
        return null;
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        RSocket rSocket = RSocketConnector
                .create()
                .reconnect(Retry.backoff(50, Duration.ofMillis(500)))
                .connect(TcpClientTransport.create(uri.getHost(), uri.getPort()))
                .block();

        return new _SocketSession(rSocket);
    }
}
