package org.noear.solon.boot.rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import reactor.core.publisher.Mono;

/**
 * @author noear 2020/12/14 created
 */
public class RsocketAcceptor implements SocketAcceptor {
    public static final RsocketAcceptor instance = new RsocketAcceptor();

    private RsocketHandler handler = new RsocketHandler();

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload connectionSetupPayload, RSocket rSocket) {
        return Mono.just(handler);
    }
}
