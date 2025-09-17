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
package org.noear.solon.server.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.Utils;
import org.noear.solon.server.prop.impl.WebSocketServerProps;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class JavaWsServer extends WebSocketServer {
    static final Logger log = LoggerFactory.getLogger(JavaWsServer.class);

    private final WebSocketServerProps wsProps;
    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    public JavaWsServer(WebSocketServerProps props, int port) {
        super(new InetSocketAddress(port));
        this.wsProps = props;
    }

    public JavaWsServer(WebSocketServerProps props, InetAddress address, int port) {
        super(new InetSocketAddress(address, port));
        this.wsProps = props;
    }

    @Override
    public void onStart() {
        log.info("Server:Websocket onStart...");
    }


    private WebSocketImpl getSession(WebSocket conn) {
        return getSession(conn, null);
    }

    private WebSocketImpl getSession(WebSocket conn, ClientHandshake shake) {
        WebSocketImpl session = conn.getAttachment();

        if (session == null) {
            //直接从附件拿，不一定可靠
            session = new WebSocketImpl(conn);
            conn.setAttachment(session);

            if (shake != null) {
                Iterator<String> httpFields = shake.iterateHttpFields();
                while (httpFields.hasNext()) {
                    String name = httpFields.next();
                    session.param(name, shake.getFieldValue(name));
                }
            }
        }

        return session;
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder tmp = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);

        //添加子协议支持
        String uri = DecodeUtils.rinseUri(request.getResourceDescriptor());
        String path = URI.create(uri).getPath();

        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            String reqProtocols = Utils.annoAlias(request.getFieldValue(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL), "");
            String protocols = subProtocolCapable.getSubProtocols(Arrays.asList(reqProtocols.split(",")));

            if (Utils.isNotEmpty(protocols)) {
                tmp.put(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL, protocols);
            }
        }

        return tmp;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake shake) {
        WebSocketImpl webSocket = getSession(conn, shake);
        webSocketRouter.getListener().onOpen(webSocket);

        //设置闲置超时
        if (wsProps.getIdleTimeout() > 0) {
            webSocket.setIdleTimeout(wsProps.getIdleTimeout());
        }
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        WebSocketImpl webSocket = getSession(conn);
        if (webSocket.isClosed()) {
            return;
        } else {
            RunUtil.runAndTry(webSocket::close);
        }

        webSocketRouter.getListener().onClose(webSocket);
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata f) {
        super.onWebsocketPing(conn, f);

        WebSocketImpl webSocket = getSession(conn);
        if (webSocket != null) {
            webSocket.onReceive();
        }

        webSocketRouter.getListener().onPing(webSocket);
    }

    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {
        super.onWebsocketPong(conn, f);

        WebSocketImpl webSocket = getSession(conn);
        if (webSocket != null) {
            webSocket.onReceive();
        }

        webSocketRouter.getListener().onPong(webSocket);
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            WebSocketImpl webSocket = getSession(conn);
            webSocket.onReceive();

            webSocketRouter.getListener().onMessage(webSocket, data);
        } catch (Exception e) {
            onError(conn, e);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            WebSocketImpl webSocket = getSession(conn);
            webSocket.onReceive();

            webSocketRouter.getListener().onMessage(webSocket, data);
        } catch (Exception e) {
            onError(conn, e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        try {
            WebSocketImpl webSocket = getSession(conn);
            webSocketRouter.getListener().onError(webSocket, ex);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}