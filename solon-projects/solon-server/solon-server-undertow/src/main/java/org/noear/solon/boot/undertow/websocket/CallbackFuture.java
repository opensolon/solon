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
package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;

import java.util.concurrent.CompletableFuture;

/**
 * 回调未来特性
 *
 * @author noear
 * @since 3.0
 */
public class CallbackFuture extends CompletableFuture<Void> implements WebSocketCallback<Void> {
    @Override
    public void complete(WebSocketChannel webSocketChannel, Void unused) {
        this.complete(null);
    }

    @Override
    public void onError(WebSocketChannel webSocketChannel, Void unused, Throwable throwable) {
        this.completeExceptionally(throwable);
    }
}
