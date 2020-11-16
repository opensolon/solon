package org.noear.solon.boot.rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.ListenerProxy;
import org.noear.solon.extend.xsocket.MessageUtils;
import reactor.core.publisher.Mono;

public class SocketAcceptorImpl implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload rPayload, RSocket rSocket) {
        Session session = _SocketSession.get(rSocket);
        Message message = MessageUtils.decode(rPayload.getData());
        if (message != null) {

            try {
                ListenerProxy.getGlobal().onMessage(session, message, false);
            } catch (Throwable ex) {
                ListenerProxy.getGlobal().onError(session, ex);
            }

        }

        return Mono.just(rSocket);
    }
}
