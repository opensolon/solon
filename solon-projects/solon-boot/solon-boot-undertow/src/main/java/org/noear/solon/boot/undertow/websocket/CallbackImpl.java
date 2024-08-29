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
package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.6
 */
class CallbackImpl implements WebSocketCallback<Void> {
    static final Logger log = LoggerFactory.getLogger(CallbackImpl.class);

    public static final WebSocketCallback<Void> instance = new CallbackImpl();

    @Override
    public void complete(WebSocketChannel webSocketChannel, Void unused) {

    }

    @Override
    public void onError(WebSocketChannel webSocketChannel, Void unused, Throwable e) {
        log.warn(e.getMessage() ,e);
    }
}
