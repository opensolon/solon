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
package org.noear.solon.net.stomp.broker.handle;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.KeyValue;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.StompSender;
import org.noear.solon.net.stomp.common.Headers;
import org.noear.solon.net.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author noear
 * @since 3.0
 */
public class StompContext extends ContextEmpty {
    private static final Logger log = LoggerFactory.getLogger(StompContext.class);

    private WebSocket session;
    private Message message;
    private String destination;
    private StompSender sender;

    public StompContext(WebSocket session, Message message, String destination, StompSender sender) {
        this.session = session;
        this.message = message;
        this.destination = destination;
        this.sender = sender;//session.attr("STOMP_MESSAGE_SENDER");

        attrSet(org.noear.solon.core.Constants.ATTR_RETURN_HANDLER, StompReturnHandler.getInstance());
    }

    @Override
    public Object request() {
        return session;
    }

    public Message getMessage() {
        return message;
    }

    public StompSender getSender() {
        return sender;
    }

    @Override
    public String sessionId() {
        return session.id();
    }

    @Override
    public String path() {
        return destination;
    }

    @Override
    public String contentType() {
        return message.getHeader(Headers.CONTENT_TYPE);
    }

    @Override
    public String body(String charset) throws IOException {
        return message.getPayload();
    }

    @Override
    public MultiMap<String> headerMap() {
        if (headerMap == null) {
            headerMap = new MultiMap<>();

            for (KeyValue<String> kv : message.getHeaderAll()) {
                headerMap.add(kv.getKey(), kv.getValue());
            }
        }

        return headerMap;
    }

    @Override
    public Object pull(Class<?> clz) {
        if (Message.class.isAssignableFrom(clz)) {
            return message;
        }

        if (WebSocket.class.isAssignableFrom(clz)) {
            return session;
        }

        return null;
    }

    /**
     * <code>
     * new StompContext(...).tryHandle();
     * </code>
     */
    public void tryHandle() {
        try {
            Handler handler = Solon.app().router().matchMain(this);
            if (handler != null) {
                handler.handle(this);
            }
        } catch (Throwable ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
}