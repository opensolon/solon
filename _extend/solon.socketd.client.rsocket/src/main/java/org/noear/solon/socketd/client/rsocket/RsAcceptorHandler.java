package org.noear.solon.socketd.client.rsocket;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

/**
 * @author noear 2021/1/4 created
 */
public class RsAcceptorHandler implements RSocket {
    Session session;
    RSocket rSocket;

    public RsAcceptorHandler(RSocket rSocket, Session session) {
        this.session = session;
        this.rSocket = rSocket;
    }

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
                Solon.global().listener().onMessage(session, message);
            } catch (Throwable ex) {
                EventBus.push(ex);
            }
        }

        return Mono.empty();
    }

    @Override
    public Mono<Void> onClose() {
        RsSocketSession.remove(rSocket);
        return Mono.empty();
    }
}
