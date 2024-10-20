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
package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.net.stomp.broker.FrameCodec;
import org.noear.solon.net.stomp.broker.FrameCodecDefault;
import org.noear.solon.net.stomp.listener.StompListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Stomp 经理人媒介
 *
 * @author noear
 * @since 3.0
 */
public class StompBrokerMedia {
    public static final FrameCodec codec = new FrameCodecDefault();

    /**
     * 服务端监听器集合
     */
    public final List<StompListener> listeners;

    /**
     * 服务端操作缓存
     */
    public final StompServerOperations operations;

    /**
     * 服务端发射器
     */
    public final StompServerEmitter emitter;

    public StompBrokerMedia() {
        listeners = new ArrayList<>();
        operations = new StompServerOperations();
        emitter = new StompServerEmitter(operations);

        listeners.add(new StompServerOperationsListener(operations, emitter));
    }
}