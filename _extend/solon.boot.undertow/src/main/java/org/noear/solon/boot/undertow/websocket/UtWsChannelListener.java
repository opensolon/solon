package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ListenerProxy;
import org.noear.solon.extend.socketd.ProtocolManager;
import org.xnio.Pooled;

import java.io.IOException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {

    public void onOpen(WebSocketChannel channel) {
        ListenerProxy.getGlobal().onOpen(_SocketServerSession.get(channel));
    }


    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage msg) throws IOException {
        try {
            Pooled<ByteBuffer[]> pulledData = msg.getData();

            try {
                ByteBuffer[] resource = pulledData.getResource();
                ByteBuffer byteBuffer = WebSockets.mergeBuffers(resource);

                Session session = _SocketServerSession.get(channel);
                Message message = null;

                if (Solon.global().enableWebSocketD()) {
                    message = ProtocolManager.decode(byteBuffer);
                } else {
                    message = Message.wrap(channel.getUrl(), null, byteBuffer.array());
                }

                ListenerProxy.getGlobal().onMessage(session, message);

            } finally {
                pulledData.discard();
            }

        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage msg) throws IOException {
        try {
            Session session = _SocketServerSession.get(channel);
            Message message = Message.wrap(channel.getUrl(), null, msg.getData());

            ListenerProxy.getGlobal().onMessage(session, message.isString(true));
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
