package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Pooled;

import java.io.IOException;
import java.nio.ByteBuffer;

public class UtWsChannelListener extends AbstractReceiveListener {
    static final Logger log = LoggerFactory.getLogger(UtWsChannelListener.class);
    private final String SESSION_KEY  ="session";

    private WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

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

        _WebSocketImpl webSocket = new _WebSocketImpl(channel);
        exchange.getRequestHeaders().forEach((k, v) -> {
            if (v.size() > 0) {
                webSocket.getHandshake().putParam(k, v.get(0));
            }
        });

        channel.setAttribute(SESSION_KEY, webSocket);
        webSocketRouter.getListener().onOpen(webSocket);
    }


    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage msg) throws IOException {
        try {
            Pooled<ByteBuffer[]> pulledData = msg.getData();

            try {
                ByteBuffer[] resource = pulledData.getResource();
                ByteBuffer byteBuffer = WebSockets.mergeBuffers(resource);

                _WebSocketImpl webSocket =  (_WebSocketImpl)channel.getAttribute(SESSION_KEY);
                webSocketRouter.getListener().onMessage(webSocket, byteBuffer);
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
            _WebSocketImpl webSocket =  (_WebSocketImpl)channel.getAttribute(SESSION_KEY);
            webSocketRouter.getListener().onMessage(webSocket, msg.getData());
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
        _WebSocketImpl webSocket =  (_WebSocketImpl)channel.getAttribute(SESSION_KEY);
        if (webSocket.isClosed()) {
            return;
        } else {
            webSocket.close();
        }
        webSocketRouter.getListener().onClose(webSocket);
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        _WebSocketImpl webSocket =  (_WebSocketImpl)channel.getAttribute(SESSION_KEY);
        webSocketRouter.getListener().onError(webSocket, error);
    }
}
