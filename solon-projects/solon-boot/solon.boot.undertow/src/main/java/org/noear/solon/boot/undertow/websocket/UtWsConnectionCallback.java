package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocketRouter;

import java.net.URI;

public class UtWsConnectionCallback implements WebSocketConnectionCallback {
    private final UtWsChannelListener listener = new UtWsChannelListener();
    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    public void onHandshake(WebSocketHttpExchange exchange){
        //添加子协议支持
        String path = URI.create(exchange.getRequestURI()).getPath();
        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            exchange.setResponseHeader("Sec-WebSocket-Protocol", subProtocolCapable.getSubProtocols());
        }
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        listener.onOpen(exchange, channel);

        channel.getReceiveSetter().set(listener);
        channel.resumeReceives();
    }
}
