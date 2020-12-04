package org.noear.solon.extend.socketd.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.Utils;
import org.noear.solon.core.message.Session;

import java.net.URI;

public class WsConnector {
    URI uri;

    public WsConnector(URI uri) {
        this.uri = uri;
    }

    public WebSocket start(Session session) {
        try {
            return new WsSocketClientImp(uri, session);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
