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
import org.noear.solon.net.stomp.Commands;
import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.broker.StompBroker;
import org.noear.solon.net.stomp.listener.SimpleStompListener;
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

    private final StompBroker broker;

    public ToHandlerStompListener(StompBroker broker) {
        this.broker = broker;
    }

    @Override
    public void onFrame(WebSocket socket, Frame frame) {
        if (Commands.SEND.equals(frame.getCommand())) {
            String destination = frame.getHeader(Headers.DESTINATION);

            if (Utils.isEmpty(destination)) {
                log.warn("This stomp message is missing route, source={}", frame.getSource());
            } else {
                try {
                    StompContext ctx = new StompContext(socket, frame, destination, broker.getServerEmitter());

                    Solon.app().tryHandle(ctx);

                    if (ctx.getHandled() || ctx.status() != 404) {
                        ctx.commit();
                    }
                } catch (Throwable e) {
                    //context 初始化时，可能会出错
                    //
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }
}