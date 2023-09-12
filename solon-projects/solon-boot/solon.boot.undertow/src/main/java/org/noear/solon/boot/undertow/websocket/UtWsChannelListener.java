package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.noear.solon.Solon;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Pooled;

import java.io.IOException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {
    static final Logger log = LoggerFactory.getLogger(UtWsChannelListener.class);

    @Override
    public void handleEvent(WebSocketChannel channel) {
        try {
            StreamSourceFrameChannel result = channel.receive();
            if (result == null) {
                if (channel.isOpen() == false) {
                    //如果已关闭，由触发关闭事件 //如果不触发，客户端关了都没感觉
                    onClose(channel, result);
                }
                return;
            }

            if (result.getType() == WebSocketFrameType.BINARY) {
                this.onBinary(channel, result);
            } else if (result.getType() == WebSocketFrameType.TEXT) {
                this.onText(channel, result);
            } else if (result.getType() == WebSocketFrameType.PONG) {
                this.onPong(channel, result);
            } else if (result.getType() == WebSocketFrameType.PING) {
                this.onPing(channel, result);
            } else if (result.getType() == WebSocketFrameType.CLOSE) {
                this.onClose(channel, result);
            }
        } catch (IOException var3) {
            this.onError(channel, var3);
        }

    }


    public void onOpen(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        Session session = _SocketServerSession.get(channel);
        exchange.getRequestHeaders().forEach((k, v) -> {
            if (v.size() > 0) {
                session.headerSet(k, v.get(0));
            }
        });

        Solon.app().listener().onOpen(session);
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

                if (Solon.app().enableWebSocketD()) {
                    message = ProtocolManager.decode(byteBuffer);
                } else {
                    message = Message.wrap(channel.getUrl(), null, byteBuffer.array());
                }

                Solon.app().listener().onMessage(session, message);

            } finally {
                pulledData.discard();
            }

        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage msg) throws IOException {
        try {
            Session session = _SocketServerSession.get(channel);
            Message message = Message.wrap(channel.getUrl(), null, msg.getData());

            Solon.app().listener().onMessage(session, message.isString(true));
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
        Solon.app().listener().onClose(_SocketServerSession.get(channel));

        _SocketServerSession.remove(channel);
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        Solon.app().listener().onError(_SocketServerSession.get(channel), error);
    }
}
