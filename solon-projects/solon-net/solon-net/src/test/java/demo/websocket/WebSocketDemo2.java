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
import org.noear.solon.net.websocket.listener.PipelineWebSocketListener;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;

import java.io.IOException;

/**
 * @author noear
 * @since 2.6
 */
@ServerEndpoint("/user/{userId}")
public class WebSocketDemo2 extends PipelineWebSocketListener {
    public WebSocketDemo2() {
        next(new SimpleWebSocketListener() {
            @Override
            public void onMessage(WebSocket socket, String text) throws IOException {
                //做个拦截
                super.onMessage(socket, text);
            }
        }).next(new SimpleWebSocketListener() {
            @Override
            public void onMessage(WebSocket socket, String text) throws IOException {
                //开始做业务
                super.onMessage(socket, text);
            }
        });
    }
}
