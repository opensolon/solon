package org.noear.solon.boot.rsocket;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ConnectorSimple;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

/**
 * @author noear 2020/12/14 created
 */
public class RsConnector extends ConnectorSimple<RSocket> {

    public RsConnector(URI uri, boolean autoReconnect) {
        super(uri, autoReconnect);
    }

    @Override
    public Class<RSocket> realType() {
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
                .acceptor(RsocketAcceptor.instance)
                .reconnect(Retry.backoff(50, Duration.ofMillis(500)))
                .connect(TcpClientTransport.create(uri().getHost(), uri().getPort()))
                .block();
    }
}
