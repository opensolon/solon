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

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.listener.SimpleStompListener;
import org.noear.solon.net.stomp.StompEmitter;
import org.noear.solon.net.stomp.Headers;
import org.noear.solon.net.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 转到 Handler 接口协议的 Listener（服务端、客户端，都可用）
 *
 * @author noear
 * @since 3.0
 */
public class ToHandlerStompListener extends SimpleStompListener {
    private static final Logger log = LoggerFactory.getLogger(ToHandlerStompListener.class);

    private final StompEmitter sender;

    public ToHandlerStompListener(StompEmitter sender) {
        this.sender = sender;
    }

    @Override
    public void onSend(WebSocket socket, Frame frame) {
        String destination = frame.getHeader(Headers.DESTINATION);

        if (Utils.isNotEmpty(destination)) {
            Context ctx = new StompContext(socket, frame, destination, sender);
            handleDo(ctx);
        }
    }

    /**
     * 处理
     */
    protected void handleDo(Context ctx) {
        try {
            Solon.app().routerHandler().handle(ctx);
        } catch (Throwable ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
}