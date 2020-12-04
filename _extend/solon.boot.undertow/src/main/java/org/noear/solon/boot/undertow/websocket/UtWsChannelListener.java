package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ListenerProxy;
import org.noear.solon.extend.socketd.MessageUtils;
import org.noear.solon.extend.socketd.MessageWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {

    public void onOpen(WebSocketChannel channel) {
        ListenerProxy.getGlobal().onOpen(_SocketServerSession.get(channel));
    }


    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage msg) throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (ByteBuffer buf : msg.getData().getResource()) {
                out.write(buf.array());
            }

            Session session = _SocketServerSession.get(channel);
            Message message = null;

            if (Solon.global().enableWebSocketD()) {
                message = MessageUtils.decode(ByteBuffer.wrap(out.toByteArray()));
            } else {
                message = MessageWrapper.wrap(channel.getUrl(), null, out.toByteArray());
            }

            ListenerProxy.getGlobal().onMessage(session, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage msg) throws IOException {
        try {
            Session session = _SocketServerSession.get(channel);
            Message message = MessageWrapper.wrap(channel.getUrl(),null,
                    msg.getData().getBytes("UTF-8"));

            ListenerProxy.getGlobal().onMessage(session, message, true);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
        ListenerProxy.getGlobal().onClose(_SocketServerSession.get(channel));

        _SocketServerSession.remove(channel);
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        ListenerProxy.getGlobal().onError(_SocketServerSession.get(channel), error);
    }
}
