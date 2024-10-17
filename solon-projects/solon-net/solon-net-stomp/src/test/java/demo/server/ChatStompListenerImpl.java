/*
 * Copyright 2017-2024 noear.org and authors
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
package demo.server;

import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.listener.SimpleStompListener;
import org.noear.solon.net.websocket.WebSocket;

/**
 * 按需扩展，比是必须
 *
 * @author noear
 * @since 2.4
 */
public class ChatStompListenerImpl extends SimpleStompListener {

    @Override
    public void onOpen(WebSocket socket) {
        String user = socket.param("user");
        if ("aaa".equals(user)) {
            socket.close();
        } else {
            System.out.println("建议放鉴权: " + user);
        }
    }


    @Override
    public void onFrame(WebSocket socket, Frame frame) {
        System.out.println(frame);
    }
}