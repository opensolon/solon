package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;


public class WebSocketHandlerImpl extends WebSocketHandler {

    @Override
    public void configure(WebSocketServletFactory factory) {
        //由监听器内个性定制
        //factory.getPolicy().setIdleTimeout(10L * 60L * 1000L);
        //factory.getPolicy().setAsyncWriteTimeout(10L * 1000L);

        //注册览听器
        //factory.register(WebSocketListenerImpl.class);

        //设置生成器（握手、子协议、监听器）
        factory.setCreator(new WebSocketCreatorImpl());
    }
}

