package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;
import org.noear.solon.core.XMethod;
import org.noear.solon.extend.socketapi.XSession;
import org.noear.solon.extend.socketapi.XSocketContextHandler;
import org.noear.solon.extend.socketapi.XSocketListener;
import org.noear.solon.extend.socketapi.XSocketMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {
    private XSocketContextHandler handler;
    private XSocketListener listener;

    public UtWsChannelListener() {
        handler = new XSocketContextHandler(XMethod.WEBSOCKET);

        Aop.getAsyn(XSocketListener.class, (bw) -> listener = bw.raw());
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
            XSocketMessage message = XSocketMessage.wrap(channel.getUrl(), out.toByteArray());

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
            XSocketMessage message = XSocketMessage.wrap(channel.getUrl(),
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
