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
package org.noear.solon.net.stomp.handle;

import org.noear.solon.Utils;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.SimpleStompListener;
import org.noear.solon.net.stomp.StompSender;
import org.noear.solon.net.stomp.impl.Headers;
import org.noear.solon.net.websocket.WebSocket;

/**
 * @author noear
 * @since 3.0
 */
public class ToHandleStompListener extends SimpleStompListener {
    private final StompSender sender;

    public ToHandleStompListener(StompSender sender) {
        this.sender = sender;
    }

    @Override
    public void onSend(WebSocket socket, Message message) {
        String destination = message.getHeader(Headers.DESTINATION);

        if(Utils.isNotEmpty(destination)) {
            //同时转发给 Solon Handler 体系
            new StompContext(socket, message, destination, sender).tryHandle();;
        }
    }
}
