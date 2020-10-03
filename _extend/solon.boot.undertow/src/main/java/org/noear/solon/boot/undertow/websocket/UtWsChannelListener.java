package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import org.noear.solon.core.*;
import org.noear.solon.extend.xsocket.XListenerProxy;
import org.noear.solon.extend.xsocket.XSocketContextHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {
    private XSocketContextHandler handler;
    private XListener listener;

    public UtWsChannelListener() {
        handler = new XSocketContextHandler(XMethod.WEBSOCKET);
        listener = XListenerProxy.getGlobal();
    }


    public void onOpen(WebSocketChannel channel) {
        if (listener != null) {
            listener.onOpen(_SocketSession.get(channel));
        }
    }


    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage msg) throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (ByteBuffer buf : msg.getData().getResource()) {
                out.write(buf.array());
            }

            XSession session = _SocketSession.get(channel);
            XMessage message = XMessage.wrap(channel.getUrl(), out.toByteArray());

            if (listener != null) {
                listener.onMessage(session, message);
            }

            if (message.getHandled() == false) {
                handler.handle(session, message, false);
            }
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage msg) throws IOException {
        try {
            XMessage message = XMessage.wrap(channel.getUrl(),
                    msg.getData().getBytes("UTF-8"));

            XSession session = _SocketSession.get(channel);

            if (listener != null) {
                listener.onMessage(session, message);
            }

            if (message.getHandled() == false) {
                handler.handle(session, message, true);
            }
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
        if (listener != null) {
            listener.onClose(_SocketSession.get(channel));
            _SocketSession.remove(channel);
        }
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        if (listener != null) {
            listener.onClose(_SocketSession.get(channel));
        }
    }
}
