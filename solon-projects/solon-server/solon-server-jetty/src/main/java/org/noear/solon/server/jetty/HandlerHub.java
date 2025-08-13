/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.server.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.noear.solon.server.jetty.websocket.WebSocketHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HandlerHub extends HandlerCollection {
    Handler http;
    Handler websocket = new WebSocketHandlerImpl();

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
