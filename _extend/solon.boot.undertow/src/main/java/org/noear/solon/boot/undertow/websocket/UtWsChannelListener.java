package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import org.noear.solon.core.Aop;
import org.noear.solonx.socket.api.XSocketListener;
import org.noear.solonx.socket.api.XSocketMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {
    protected XSocketListener listener;

    public UtWsChannelListener() {
        Aop.getAsyn(XSocketListener.class, (bw) -> listener = bw.raw());
    }


    public void onOpen(WebSocketChannel channel) {
        if (listener != null) {
            listener.onOpen(_SocketSession.get(channel));
        }
    }


    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
        if (listener != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (ByteBuffer buf : message.getData().getResource()) {
                out.write(buf.array());
            }

            listener.onMessage(_SocketSession.get(channel),
                    XSocketMessage.wrap(channel.getUrl(),
                            out.toByteArray()));
        }
        //super.onFullBinaryMessage(channel, message);
    }

    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
        if (listener != null) {
            listener.onMessage(_SocketSession.get(channel),
                    XSocketMessage.wrap(channel.getUrl(),
                            message.getData().getBytes("UTF-8")));
        }

        //super.onFullTextMessage(channel, message);
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
        if (listener != null) {
            listener.onClose(_SocketSession.get(channel));
            _SocketSession.remove(channel);
        }

        //super.onClose(webSocketChannel, channel);
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        if (listener != null) {
            listener.onClose(_SocketSession.get(channel));
        }
        //super.onError(channel, error);
    }
}
