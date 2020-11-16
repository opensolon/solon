package org.noear.solon.boot.rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.extend.xsocket.XListenerProxy;
import org.noear.solon.extend.xsocket.XMessageUtils;
import reactor.core.publisher.Mono;

public class SocketAcceptorImpl implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload rPayload, RSocket rSocket) {
        MessageSession session = _SocketSession.get(rSocket);
        Message message = XMessageUtils.decode(rPayload.getData());
        if (message != null) {

            try {
                XListenerProxy.getGlobal().onMessage(session, message, false);
            } catch (Throwable ex) {
                XListenerProxy.getGlobal().onError(session, ex);
            }

        }

        return Mono.just(rSocket);
    }
}
