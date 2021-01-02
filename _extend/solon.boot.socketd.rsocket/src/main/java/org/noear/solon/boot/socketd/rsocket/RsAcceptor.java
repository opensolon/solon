package org.noear.solon.boot.socketd.rsocket;

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
    public static final RsAcceptor instance = new RsAcceptor();

    //
    // SocketAcceptor
    //
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload connectionSetupPayload, RSocket rSocket) {

        //open
        Session session = _SocketSession.get(rSocket);
        ListenerProxy.getGlobal().onOpen(session);

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
            Session session = _SocketSession.get(this);

            try {
                ListenerProxy.getGlobal().onMessage(session, message);
            } catch (Throwable ex) {
                EventBus.push(ex);
            }
        }

        return Mono.empty();
    }

    @Override
    public Mono<Void> onClose() {
        _SocketSession.remove(this);
        return Mono.empty();
    }
}
