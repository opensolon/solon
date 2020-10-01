package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.noear.solon.boot.jetty.websocket.WebSocketHandlerImp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HandlerHolder extends AbstractHandler {
    Handler http;
    Handler websocket = new WebSocketHandlerImp();

    public HandlerHolder(Handler http) {
        this.http = http;
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        if (request.getScheme().contains("http")) {
            http.handle(s, request, req, res);
        } else {
            websocket.handle(s, request, req, res);
        }
    }
}
