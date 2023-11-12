package org.noear.solon.boot.websocket.simple;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author noear
 * @since 2.0
 */
public class SimpleWebSocketClient extends WebSocketClient {
    public SimpleWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public SimpleWebSocketClient(String serverUri) {
        super(URI.create(serverUri));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
