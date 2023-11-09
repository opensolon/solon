package org.noear.solon.net.websocket.impl;

import org.noear.solon.net.websocket.WebSocketListener;

/**
 * @author noear
 * @since 2.5
 */
public class WebSocketListenerHolder {
    private String path;
    private WebSocketListener listener;

    public WebSocketListenerHolder(String path, WebSocketListener listener){
        this.path = path;
        this.listener = listener;
    }

    public String getPath() {
        return path;
    }

    public WebSocketListener getListener() {
        return listener;
    }
}
