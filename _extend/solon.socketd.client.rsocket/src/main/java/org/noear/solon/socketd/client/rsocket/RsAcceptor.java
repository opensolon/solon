package org.noear.solon.socketd.client.rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import org.noear.solon.Solon;
import org.noear.solon.core.message.Session;
import reactor.core.publisher.Mono;

/**
 * @author noear 2020/12/14 created
 */
public class RsAcceptor implements SocketAcceptor {
    public RsAcceptor() {

    }

    private Session session;

    public RsAcceptor(Session session) {
        this.session = session;
    }

    //
    // SocketAcceptor
    //
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload connectionSetupPayload, RSocket rSocket) {
        Session session1;

        if (session == null) {
            //for server
            session1 = RsSocketSession.get(rSocket);
        } else {
            //for client
            session1 = session;
        }


        Solon.global().listener().onOpen(session1);

        return Mono.just(new RsAcceptorHandler(rSocket, session1));
    }
}
