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
package org.noear.solon.net.stomp.broker.listener;

import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.websocket.WebSocket;

/**
 * 简单消息监听器
 *
 * @author noear
 * @since 2.7
 * @since 3.0
 */
public class SimpleStompServerListener implements StompServerListener {

    @Override
    public void onOpen(WebSocket socket) {

    }

    @Override
    public void onClose(WebSocket socket) {

    }

    @Override
    public void onConnect(WebSocket socket, Frame frame) {

    }

    @Override
    public void onDisconnect(WebSocket socket, Frame frame) {

    }

    @Override
    public void onSubscribe(WebSocket socket, Frame frame) {

    }

    @Override
    public void onUnsubscribe(WebSocket socket, Frame frame) {

    }

    @Override
    public void onSend(WebSocket socket, Frame frame) {

    }

    @Override
    public void onAck(WebSocket socket, Frame frame) {

    }
}
