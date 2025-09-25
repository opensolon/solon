package org.noear.solon.server.grizzly.websocket;

import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.noear.solon.Utils;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.noear.solon.server.util.DecodeUtils;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author noear
 * @since 3.6
 */
public class GyWebSocketApplication extends WebSocketApplication {
    private static final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners) {
        GyWebSocket gyWebSocket = new GyWebSocket(handler, requestPacket, listeners);
        gyWebSocket.add(new GyWebSocketListenerImpl());
        return gyWebSocket;
    }

    @Override
    protected void handshake(HandShake handshake) throws HandshakeException {
        super.handshake(handshake);

        //添加子协议支持
        String path = handshake.getResourcePath();
        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            String protocols = subProtocolCapable.getSubProtocols(handshake.getSubProtocol());

            if (Utils.isNotEmpty(protocols)) {
                handshake.setSubProtocol(Arrays.asList(protocols.split(",")));
            }
        }
    }

    @Override
    public List<String> getSupportedProtocols(List<String> subProtocol) {
        return subProtocol;
    }

    @Override
    protected boolean onError(WebSocket socket, Throwable t) {
        GyWebSocket gyWebSocket = (GyWebSocket) socket;
        webSocketRouter.getListener().onError(gyWebSocket.attachment, t);

        return super.onError(socket, t);
    }
}
