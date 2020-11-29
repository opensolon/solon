package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.ListenerProxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {

    public void onOpen(WebSocketChannel channel) {
        ListenerProxy.getGlobal().onOpen(_SocketSession.get(channel));
    }


    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage msg) throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (ByteBuffer buf : msg.getData().getResource()) {
                out.write(buf.array());
            }

            Session session = _SocketSession.get(channel);
            Message message = Message.wrap(channel.getUrl(), null,out.toByteArray());

            ListenerProxy.getGlobal().onMessage(session, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage msg) throws IOException {
        try {
            Session session = _SocketSession.get(channel);
            Message message = Message.wrap(channel.getUrl(),null,
                    msg.getData().getBytes("UTF-8"));

            ListenerProxy.getGlobal().onMessage(session, message, true);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
        ListenerProxy.getGlobal().onClose(_SocketSession.get(channel));

        _SocketSession.remove(channel);
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        ListenerProxy.getGlobal().onError(_SocketSession.get(channel), error);
    }
}
