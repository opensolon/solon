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
package org.noear.solon.net.stomp.broker;

import org.noear.solon.Utils;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.lang.Nullable;
import org.noear.solon.net.stomp.Commands;
import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.Headers;
import org.noear.solon.net.stomp.broker.impl.StompSessionImpl;
import org.noear.solon.net.stomp.broker.impl.StompBrokerMedia;
import org.noear.solon.net.stomp.listener.StompListener;
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

    //服务端操作缓存
    private final StompBrokerMedia brokerMedia;

    protected ToStompWebSocketListener(String endpoint, StompBrokerMedia brokerMedia) {
        if (endpoint == null) {
            throw new IllegalArgumentException("Endpoint is not empty");
        }

        this.brokerMedia = brokerMedia;
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
        StompSessionImpl session = StompSessionImpl.of(socket);

        for (RankEntity<StompListener> listener : brokerMedia.listeners) {
            listener.target.onOpen(session);
        }
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        String txt = Charset.forName("UTF-8").decode(binary).toString();
        this.onMessage(socket, txt);
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        StompSessionImpl session = StompSessionImpl.of(socket);

        AtomicBoolean decodeOk = new AtomicBoolean(Boolean.FALSE);

        StompBrokerMedia.codec.decode(text, frame -> {
            decodeOk.set(Boolean.TRUE);
            onStomp(session, frame);

            //统一处理凭据
            session.receipt(frame);
        });

        if (decodeOk.get() == false) {
            //解码失败
            if (log.isDebugEnabled()) {
                log.debug("Session ping, {}", socket.id());
            }

            //可能是ping，响应
            session.send(Frame.newBuilder().command(Commands.MESSAGE).payload(text).build());
        }
    }

    @Override
    public void onClose(WebSocket socket) {
        try {
            StompSessionImpl session = StompSessionImpl.of(socket);

            for (RankEntity<StompListener> listener : brokerMedia.listeners) {
                listener.target.onClose(session);
            }
        } finally {
            socket.attrMap().clear();
        }
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        StompSessionImpl session = StompSessionImpl.of(socket);

        for (RankEntity<StompListener> listener : brokerMedia.listeners) {
            try {
                listener.target.onError(session, error);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Stomp 帧接收
     */
    protected void onStomp(StompSessionImpl session, Frame frame) {
        String txId = frame.getHeader(Headers.TRANSACTION);

        if (Commands.BEGIN.equals(frame.getCommand())) {
            //事务开始
            String txAttrKey = Headers.TRANSACTION + ":" + txId;
            session.getSocket().attr(txAttrKey, new ArrayList<Frame>());

            //答复凭据
            //session.receipt(frame);
        } else if (Commands.COMMIT.equals(frame.getCommand())) {
            //事务提交
            String txAttrKey = Headers.TRANSACTION + ":" + txId;
            List<Frame> frameList = session.getSocket().attr(txAttrKey);
            try {
                if (frameList != null && frameList.size() > 0) {
                    for (Frame f : frameList) {
                        stompToListener(session, f);
                    }
                }
            } finally {
                //移除事务缓存
                session.getSocket().attrMap().remove(txAttrKey);
            }

            //答复凭据
            //session.receipt(frame);
        } else if (Commands.ABORT.equals(frame.getCommand())) {
            //事务回滚
            String txAttrKey = Headers.TRANSACTION + ":" + txId;
            //移除事务缓存
            session.getSocket().attrMap().remove(txAttrKey);

            //答复凭据
            //session.receipt(frame);
        } else if (txId != null) {
            //事务中
            String txAttrKey = Headers.TRANSACTION + ":" + txId;
            List<Frame> frameList = (List<Frame>) session.getSocket().attrMap().computeIfAbsent(txAttrKey, k -> new ArrayList());
            frameList.add(frame);
        } else {
            stompToListener(session, frame);
        }
    }

    /**
     * Stomp 帧转给监听器
     */
    protected void stompToListener(StompSessionImpl session, Frame frame) {
        for (RankEntity<StompListener> listener : brokerMedia.listeners) {
            try {
                listener.target.onFrame(session, frame);
            } catch (Throwable e) {
                onError(session.getSocket(), e);
            }
        }
    }
}