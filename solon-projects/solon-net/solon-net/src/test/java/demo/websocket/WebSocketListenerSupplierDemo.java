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
package demo.websocket;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.impl.ConfigDefault;
import org.noear.solon.net.websocket.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketListenerSupplier;
import org.noear.solon.net.websocket.socketd.ToSocketdWebSocketListener;

import java.io.IOException;

/**
 * @author noear
 * @since 2.7
 */
public class WebSocketListenerSupplierDemo implements WebSocketListenerSupplier, Listener {
    private ToSocketdWebSocketListener webSocketListener;

    @Override
    public WebSocketListener getWebSocketListener() {
        if (webSocketListener == null) {
            webSocketListener = new ToSocketdWebSocketListener(new ConfigDefault(false), this);
        }

        return webSocketListener;
    }

    @Override
    public void onOpen(Session session) throws IOException {

    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {

    }

    @Override
    public void onClose(Session session) {

    }

    @Override
    public void onError(Session session, Throwable error) {

    }
}
