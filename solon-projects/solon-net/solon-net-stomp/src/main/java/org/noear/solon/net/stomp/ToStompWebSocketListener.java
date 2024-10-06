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
package org.noear.solon.net.stomp;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.lang.Nullable;
import org.noear.solon.net.stomp.impl.*;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * websocket 转 stomp 监听器
 *
 * @author limliu
 * @since 2.7
 */
public class ToStompWebSocketListener implements WebSocketListener, SubProtocolCapable {
    static Logger log = LoggerFactory.getLogger(ToStompWebSocketListener.class);

    private final List<StompListener> listenerList = new ArrayList<>();
    private final StompBrokerSender messageSender;

    protected ToStompWebSocketListener(String endpoint) {
        if (endpoint == null) {
            throw new IllegalArgumentException("Endpoint is not empty");
        }

        this.messageSender = new StompBrokerSender();

        BeanWrap bw = Solon.context().wrap(endpoint, this.messageSender);
        Solon.context().putWrap(endpoint, bw);
        Solon.context().putWrap(StompSender.class, bw);

        this.addListener(new StompBrokerListener(this.messageSender));
    }

    public void addListener(StompListener... listeners) {
        for (StompListener listener : listeners) {
            listenerList.add(listener);
        }
    }

    public StompSender getSender() {
        return messageSender;
    }

    @Override
    public String getSubProtocols(@Nullable Collection<String> requestProtocols) {
        if (Utils.isEmpty(requestProtocols)) {
            //如果没有子协议要求，则不限制
            return null;
        } else {
            //如果有子协议要求，只允许 stomp
            return "stomp";
        }
    }

    @Override
    public void onOpen(WebSocket socket) {
        for (StompListener listener : listenerList) {
            listener.onOpen(socket);
        }
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        AtomicBoolean atomicBoolean = new AtomicBoolean(Boolean.TRUE);

        messageSender.getOperations().getMsgCodec().decode(text, msg -> {
            atomicBoolean.set(Boolean.FALSE);
            String command = msg.getCommand() == null ? "" : msg.getCommand();
            switch (command) {
                case Commands.CONNECT: {
                    for (StompListener listener : listenerList) {
                        listener.onConnect(socket, msg);
                    }
                    break;
                }
                case Commands.DISCONNECT: {
                    for (StompListener listener : listenerList) {
                        listener.onDisconnect(socket, msg);
                    }
                    break;
                }
                case Commands.SUBSCRIBE: {
                    for (StompListener listener : listenerList) {
                        listener.onSubscribe(socket, msg);
                    }
                    break;
                }
                case Commands.UNSUBSCRIBE: {
                    for (StompListener listener : listenerList) {
                        listener.onUnsubscribe(socket, msg);
                    }
                    break;
                }
                case Commands.SEND: {
                    for (StompListener listener : listenerList) {
                        listener.onSend(socket, msg);
                    }
                    break;
                }
                case Commands.ACK:
                case Commands.NACK: {
                    for (StompListener listener : listenerList) {
                        listener.onAck(socket, msg);
                    }
                    break;
                }
                default: {
                    //未知命令
                    log.warn("session unknown, {}\r\n{}", socket.id(), text);

                    doSend(socket, Message.newBuilder().command(Commands.UNKNOWN).payload(text).build());
                }
            }
        });

        if (atomicBoolean.get()) {
            if (log.isDebugEnabled()) {
                log.debug("session ping, {}", socket.id());
            }
            //可能是ping，响应
            doSend(socket, Message.newBuilder().command(Commands.MESSAGE).payload(text).build());
        }
    }

    protected void doSend(WebSocket socket, Message message) {
        messageSender.sendTo(socket, message);
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        String txt = Charset.forName("UTF-8").decode(binary).toString();
        this.onMessage(socket, txt);
    }

    @Override
    public void onClose(WebSocket socket) {
        for (StompListener listener : listenerList) {
            listener.onClose(socket);
        }
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        log.error("", error);
    }
}