package org.noear.solon.boot.rsocket;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ListenerProxy;
import org.noear.solon.extend.socketd.MessageUtils;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author noear 2020/12/14 created
 * @since 1.2
 */
public class RsocketHandler implements RSocket {

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

            Message message = MessageUtils.decode(byteBuffer);
            Session session = _SocketSession.get(this);

            try {
                ListenerProxy.getGlobal().onMessage(session, message, false);
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
