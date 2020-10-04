package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebSocketHandlerImp extends WebSocketHandler {

    @Override
    public void configure(WebSocketServletFactory factory) {
        //factory.getPolicy().setIdleTimeout(10L * 60L * 1000L);
        //factory.getPolicy().setAsyncWriteTimeout(10L * 1000L);

        //注册览听器
        factory.register(WebSocketListenerImp.class);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.handle(target, baseRequest, request, response);
    }
}

