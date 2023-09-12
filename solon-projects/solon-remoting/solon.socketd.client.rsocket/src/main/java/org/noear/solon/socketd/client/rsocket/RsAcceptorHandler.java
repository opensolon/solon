package org.noear.solon.socketd.client.rsocket;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import org.noear.solon.Solon;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

/**
 * @author noear 2021/1/4 created
 */
public class RsAcceptorHandler implements RSocket {
    static final Logger log = LoggerFactory.getLogger(RsAcceptorHandler.class);

    private Session session;
    private RSocket rSocket;

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
                Solon.app().listener().onMessage(session, message);
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
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
