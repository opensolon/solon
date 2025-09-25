package org.noear.solon.server.grizzly.websocket;

import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocketListener;

/**
 *
 * @author noear 2025/9/25 created
 *
 */
public class GyWebSocket extends DefaultWebSocket {
    protected org.noear.solon.net.websocket.WebSocket attachment;

    public GyWebSocket(ProtocolHandler protocolHandler, HttpRequestPacket request, WebSocketListener... listeners) {
        super(protocolHandler, request, listeners);
    }
}
