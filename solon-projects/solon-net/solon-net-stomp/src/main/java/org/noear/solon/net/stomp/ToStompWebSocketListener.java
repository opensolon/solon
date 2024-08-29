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
import org.noear.solon.net.annotation.ServerEndpoint;
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
 * websocket转stomp处理
 *
 * @author limliu
 * @since 2.7
 */
public abstract class ToStompWebSocketListener implements WebSocketListener, SubProtocolCapable {
    static Logger log = LoggerFactory.getLogger(StompListenerImpl.class);

    private List<StompListener> listenerList = new ArrayList<>();
    private StompMessageOperations stompMessageOperations;
    protected StompMessageSendingTemplate stompMessageSendingTemplate;

    public ToStompWebSocketListener() {
        this(null);
    }

    public ToStompWebSocketListener(StompListener listener) {
        ServerEndpoint serverEndpoint = getClass().getAnnotation(ServerEndpoint.class);
        if(serverEndpoint == null || Utils.isEmpty(serverEndpoint.value())){
            throw new RuntimeException("Path is not null");
        }
        this.stompMessageOperations = new StompMessageOperations();
        this.stompMessageSendingTemplate = new StompMessageSendingTemplate(stompMessageOperations);
        BeanWrap bw = Solon.context().wrap(StompMessageSendingTemplate.class, this.stompMessageSendingTemplate);
        Solon.context().beanRegister(bw, serverEndpoint.value(), true);
        this.addListener(new StompListenerImpl(stompMessageOperations, this.stompMessageSendingTemplate), listener);
    }

    public void addListener(StompListener... listeners) {
        if (listeners == null || listeners.length == 0) {
            return;
        }
        for (StompListener listener : listeners) {
            if (listener == null) {
                continue;
            }
            listenerList.add(listener);
        }
    }

    @Override
    public String getSubProtocols(@Nullable Collection<String> requestProtocols) {
        return "stomp";
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
        stompMessageOperations.getMsgCodec().decode(text, msg -> {
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
                    doSend(socket, new MessageImpl(Commands.UNKNOWN, text));
                }
            }
        });

        if (atomicBoolean.get()) {
            if (log.isDebugEnabled()) {
                log.debug("session ping, {}", socket.id());
            }
            //可能是ping，响应
            doSend(socket, new MessageImpl(Commands.MESSAGE, text));
        }
    }

    protected void doSend(WebSocket socket, Message message) {
        stompMessageSendingTemplate.send(socket, message);
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