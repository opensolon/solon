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
package demo.websocket;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.listener.PathWebSocketListener;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;

import java.io.IOException;

/**
 * @author noear
 * @since 2.6
 */
@ServerEndpoint
public class WebSocketDemo3 extends PathWebSocketListener {
    public WebSocketDemo3() {
        of("/admin", new SimpleWebSocketListener() {
            @Override
            public void onOpen(WebSocket socket) {
                //给管理频道做个签权
                super.onOpen(socket);
            }
        });
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        super.onMessage(socket, text);

        //统一处理消息
    }
}
