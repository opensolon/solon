package org.noear.solon.socketd.client.rsocket;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ConnectorBase;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

/**
 * @author noear 2020/12/14 created
 */
class RsConnector extends ConnectorBase<RSocket> {

    public RsConnector(URI uri, boolean autoReconnect) {
        super(uri, autoReconnect);
    }

    @Override
    public Class<RSocket> driveType() {
        return RSocket.class;
    }

    @Override
    public boolean autoReconnect() {
        return false;
    }

    @Override
    public RSocket open(Session session) throws IOException {
        return RSocketConnector
                .create()
                .acceptor(new RsAcceptor(session))
                .reconnect(Retry.backoff(50, Duration.ofMillis(500)))
                .connect(TcpClientTransport.create(uri().getHost(), uri().getPort()))
                .block();
    }
}
