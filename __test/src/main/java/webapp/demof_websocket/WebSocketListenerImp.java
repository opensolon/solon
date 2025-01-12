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
package webapp.demof_websocket;

import org.noear.nami.channel.socketd.SocketdProxy;
import org.noear.socketd.transport.core.impl.ConfigDefault;
import org.noear.solon.Solon;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.listener.PipelineWebSocketListener;
import org.noear.solon.net.websocket.listener.PathWebSocketListener;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;
import org.noear.solon.net.websocket.socketd.ToSocketdWebSocketListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint
public class WebSocketListenerImp extends PipelineWebSocketListener {
    public WebSocketListenerImp() {
        next(new SimpleWebSocketListener() {
            private Set<WebSocket> sessionMap = new HashSet<>();

            @Override
            public void onMessage(WebSocket session, String message) {
                System.out.println(session.path());

                if (Solon.cfg().isDebugMode()) {
                    return;
                }

                sessionMap.forEach(s -> {
                    s.send(message);
                });
            }

            @Override
            public void onOpen(WebSocket session) {
                sessionMap.add(session);
            }

            @Override
            public void onClose(WebSocket session) {
                sessionMap.remove(session);
            }
        }).next(new PathWebSocketListener().of("/demof/websocket/{id}", new SimpleWebSocketListener() {
            @Override
            public void onMessage(WebSocket socket, String text) throws IOException {
                socket.send("你好");
            }
        })).next(new ToSocketdWebSocketListener(new ConfigDefault(false), SocketdProxy
                .socketdToHandler));
    }
}
