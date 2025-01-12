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
package org.noear.solon.net.websocket.listener;

import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author noear
 * @since 2.0
 */
public class PipelineWebSocketListener implements WebSocketListener {
    protected final Deque<WebSocketListener> pipeline = new LinkedList<>();
    protected final Set<Class<?>> typeSet = new HashSet<>();

    /**
     * 前一个
     */
    public PipelineWebSocketListener prev(WebSocketListener listener) {
        pipeline.addFirst(listener);
        return this;
    }

    /**
     * 前一个
     */
    public PipelineWebSocketListener prevIfAbsent(WebSocketListener listener) {
        if (typeSet.contains(listener.getClass()) == false) {
            typeSet.add(listener.getClass());
            pipeline.addFirst(listener);
        }


        return this;
    }

    /**
     * 后一个
     */
    public PipelineWebSocketListener next(WebSocketListener listener) {
        pipeline.addLast(listener);
        return this;
    }

    /**
     * 后一个
     */
    public PipelineWebSocketListener nextIfAbsent(WebSocketListener listener) {
        if (typeSet.contains(listener.getClass()) == false) {
            typeSet.add(listener.getClass());
            pipeline.addLast(listener);
        }

        return this;
    }

    @Override
    public void onOpen(WebSocket s) {
        for (WebSocketListener listener : pipeline) {
            listener.onOpen(s);
        }
    }

    @Override
    public void onMessage(WebSocket s, String text) throws IOException {
        for (WebSocketListener listener : pipeline) {
            listener.onMessage(s, text);
        }
    }

    @Override
    public void onMessage(WebSocket s, ByteBuffer binary) throws IOException {
        for (WebSocketListener listener : pipeline) {
            listener.onMessage(s, binary);
        }
    }

    @Override
    public void onClose(WebSocket s) {
        for (WebSocketListener listener : pipeline) {
            listener.onClose(s);
        }
    }

    @Override
    public void onError(WebSocket s, Throwable error) {
        for (WebSocketListener listener : pipeline) {
            listener.onError(s, error);
        }
    }

    @Override
    public void onPing(WebSocket s) {
        for (WebSocketListener listener : pipeline) {
            listener.onPing(s);
        }
    }

    @Override
    public void onPong(WebSocket s) {
        for (WebSocketListener listener : pipeline) {
            listener.onPong(s);
        }
    }
}
