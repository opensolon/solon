package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.noear.solon.boot.jetty.websocket.WebSocketHandlerImp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HandlerHub extends HandlerCollection {
    Handler http;
    Handler websocket = new WebSocketHandlerImp();

    final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";

    public HandlerHub(Handler http) {
        this.http = http;
        addHandler(http);
        addHandler(websocket);
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        if(req.getHeader(SEC_WEBSOCKET_KEY) != null){
            websocket.handle(s, request, req, res);
        }else{
            http.handle(s, request, req, res);
        }
    }
}
