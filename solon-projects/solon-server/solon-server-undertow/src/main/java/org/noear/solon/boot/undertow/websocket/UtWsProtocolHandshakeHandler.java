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


import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.util.Methods;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketLogger;
import io.undertow.websockets.core.protocol.Handshake;
import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
import io.undertow.websockets.core.protocol.version08.Hybi08Handshake;
import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
import io.undertow.websockets.extensions.ExtensionHandshake;
import io.undertow.websockets.spi.AsyncWebSocketHttpServerExchange;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.xnio.StreamConnection;

/**
 * @author noear
 * @since 2.8
 */
public class UtWsProtocolHandshakeHandler implements HttpHandler {
    private final Set<Handshake> handshakes;
    private final HttpUpgradeListener upgradeListener;
    private final UtWsConnectionCallback callback;
    private final Set<WebSocketChannel> peerConnections;
    private final HttpHandler next;

    public UtWsProtocolHandshakeHandler(HttpHandler next) {
        this.peerConnections = Collections.newSetFromMap(new ConcurrentHashMap());
        this.callback = new UtWsConnectionCallback();
        Set<Handshake> handshakes = new HashSet();
        handshakes.add(new Hybi13Handshake());
        handshakes.add(new Hybi08Handshake());
        handshakes.add(new Hybi07Handshake());
        this.handshakes = handshakes;
        this.next = next;
        this.upgradeListener = null;
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (!exchange.getRequestMethod().equals(Methods.GET)) {
            this.next.handleRequest(exchange);
        } else {
            final AsyncWebSocketHttpServerExchange facade = new AsyncWebSocketHttpServerExchange(exchange, this.peerConnections);
            Handshake handshaker = null;
            Iterator var4 = this.handshakes.iterator();

            while(var4.hasNext()) {
                Handshake method = (Handshake)var4.next();
                if (method.matches(facade)) {
                    handshaker = method;
                    break;
                }
            }

            if (handshaker == null) {
                this.next.handleRequest(exchange);
            } else {
                WebSocketLogger.REQUEST_LOGGER.debugf("Attempting websocket handshake with %s on %s", handshaker, exchange);
                final Handshake selected = handshaker;
                if (this.upgradeListener == null) {
                    exchange.upgradeChannel(new HttpUpgradeListener() {
                        public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
                            WebSocketChannel channel = selected.createChannel(facade, streamConnection, facade.getBufferPool());
                            UtWsProtocolHandshakeHandler.this.peerConnections.add(channel);
                            UtWsProtocolHandshakeHandler.this.callback.onConnect(facade, channel);
                        }
                    });
                } else {
                    exchange.upgradeChannel(this.upgradeListener);
                }

                //
                callback.onHandshake(facade);

                handshaker.handshake(facade);
            }

        }
    }

    public Set<WebSocketChannel> getPeerConnections() {
        return this.peerConnections;
    }

    public UtWsProtocolHandshakeHandler addExtension(ExtensionHandshake extension) {
        if (extension != null) {
            Iterator var2 = this.handshakes.iterator();

            while(var2.hasNext()) {
                Handshake handshake = (Handshake)var2.next();
                handshake.addExtension(extension);
            }
        }

        return this;
    }
}