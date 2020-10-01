package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketHandlerImp extends WebSocketHandler {
    @Override
    public void configure(WebSocketServletFactory factory) {
        //factory.getPolicy().setIdleTimeout(10L * 60L * 1000L);
        //factory.getPolicy().setAsyncWriteTimeout(10L * 1000L);

        /* 设置自定义的WebSocket组合 */
        factory.register(WebSocketListenerImp.class);
        final WebSocketCreator creator = factory.getCreator();

        factory.setCreator((req, res) -> creator.createWebSocket(req, res));
    }
}

