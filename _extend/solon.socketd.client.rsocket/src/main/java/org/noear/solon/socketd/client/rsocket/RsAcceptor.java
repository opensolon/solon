package org.noear.solon.socketd.client.rsocket;

import io.netty.buffer.ByteBuf;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ListenerProxy;
import org.noear.solon.socketd.ProtocolManager;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

/**
 * @author noear 2020/12/14 created
 */
public class RsAcceptor implements SocketAcceptor, RSocket {
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

        if (session == null) {
            //for server
            Session session1 = RsSocketSession.get(rSocket);
            ListenerProxy.getGlobal().onOpen(session1);
        } else {
            //for client
            ListenerProxy.getGlobal().onOpen(session);
        }

        return Mono.just(this);
    }

    //
    // RSocket
    //
    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        ByteBuf byteBuf = payload.data();
        int len = byteBuf.readInt();

        if (len > 0) {
            byte[] bytes = new byte[len - Integer.BYTES];
            byteBuf.readBytes(bytes);

            ByteBuffer byteBuffer = ByteBuffer.allocate(len);
            byteBuffer.putInt(len);
            byteBuffer.put(bytes);
            byteBuffer.flip();

            Message message = ProtocolManager.decode(byteBuffer);

            try {
                if (session == null) {
                    Session session1 = RsSocketSession.get(this);
                    ListenerProxy.getGlobal().onMessage(session1, message);
                } else {
                    ListenerProxy.getGlobal().onMessage(session, message);
                }
            } catch (Throwable ex) {
                EventBus.push(ex);
            }
        }

        return Mono.empty();
    }

    @Override
    public Mono<Void> onClose() {
        if (session == null) {
            RsSocketSession.remove(this);
        }

        return Mono.empty();
    }
}
