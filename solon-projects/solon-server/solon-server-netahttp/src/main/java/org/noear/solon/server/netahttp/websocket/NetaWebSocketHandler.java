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
package org.noear.solon.server.netahttp.websocket;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.ProtoContext;
import net.hasor.neta.channel.ProtoDuplexer;
import net.hasor.neta.channel.ProtoStatus;
import net.hasor.neta.channel.data.ProtoRcvQueue;
import net.hasor.neta.channel.data.ProtoSndQueue;
import net.hasor.neta.codec.http.websocket.*;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 基于 neta 的 WebSocket 事件处理器
 * <p>
 * 放置在 WebSocketMessageDuplexer 之后，
 * 接收 WebSocketMessage 并转发给 Solon WebSocket 路由。
 *
 * @author noear
 * @since 3.10
 */
public class NetaWebSocketHandler implements ProtoDuplexer<WebSocketMessage, Object, Object, WebSocketMessage> {
    private static final Logger log = LoggerFactory.getLogger(NetaWebSocketHandler.class);
    private static final Class<NetaWebSocketImpl> WS_KEY = NetaWebSocketImpl.class;

    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void onInit(String pipelineId, int srcId, int dstId, ProtoContext ctx) throws Throwable {
        // 创建 WebSocket 会话
        String uri = buildUri(ctx);
        NetaWebSocketImpl ws = new NetaWebSocketImpl(ctx, uri);

        // 存储到上下文
        ctx.context(WS_KEY, ws);
        webSocketRouter.getListener().onOpen(ws);
    }

    @Override
    public ProtoStatus onMessage(ProtoContext ctx, boolean isRcv,
                                 ProtoRcvQueue<WebSocketMessage> rcvUp, ProtoSndQueue<Object> rcvDown,
                                 ProtoRcvQueue<Object> sndUp, ProtoSndQueue<WebSocketMessage> sndDown) throws Throwable {
        NetaWebSocketImpl ws = ctx.context(WS_KEY);
        if (ws == null) {
            return ProtoStatus.Stop;
        }

        if (!isRcv) {
            return ProtoStatus.Next;
        }

        ProtoRcvQueue<WebSocketMessage> queue = rcvUp;
        WebSocketMessage msg;
        while ((msg = queue.takeMessage()) != null) {
            if (msg instanceof TextWebSocketMessage) {
                TextWebSocketMessage textMsg = (TextWebSocketMessage) msg;
                ByteBuf buf = textMsg.content();
                String text = new String(buf.asByteArray(), StandardCharsets.UTF_8);
                ws.onReceive();
                webSocketRouter.getListener().onMessage(ws, text);
            } else if (msg instanceof BinaryWebSocketMessage) {
                BinaryWebSocketMessage binMsg = (BinaryWebSocketMessage) msg;
                ByteBuf buf = binMsg.content();
                java.nio.ByteBuffer data = java.nio.ByteBuffer.wrap(buf.asByteArray());
                ws.onReceive();
                webSocketRouter.getListener().onMessage(ws, data);
            } else if (msg instanceof PingWebSocketEvent) {
                ws.onReceive();
                webSocketRouter.getListener().onPing(ws);
            } else if (msg instanceof PongWebSocketEvent) {
                ws.onReceive();
                webSocketRouter.getListener().onPong(ws);
            } else if (msg instanceof WebSocketCloseEvent) {
                try {
                    if (!ws.isClosed()) {
                        ws.close();
                    }
                } catch (Throwable ignore) {
                }
                webSocketRouter.getListener().onClose(ws);
                return ProtoStatus.Stop;
            }
            msg.release();
        }
        return ProtoStatus.Next;
    }

    @Override
    public ProtoStatus onError(ProtoContext ctx, boolean isRcv, Throwable error,
                               net.hasor.neta.channel.ProtoExceptionHolder errorHolder) throws Throwable {
        NetaWebSocketImpl ws = ctx.context(WS_KEY);
        if (ws != null) {
            webSocketRouter.getListener().onError(ws, error);
        }
        return ProtoStatus.Stop;
    }

    @Override
    public void onClose(ProtoContext ctx) {
        NetaWebSocketImpl ws = ctx.context(WS_KEY);
        if (ws != null && !ws.isClosed()) {
            try {
                ws.close();
            } catch (Throwable ignore) {
            }
            webSocketRouter.getListener().onClose(ws);
        }
    }

    private String buildUri(ProtoContext ctx) {
        try {
            WebSocketContext wsCtx = ctx.context(WebSocketContext.class);
            if (wsCtx != null) {
                String path = wsCtx.requestPath();
                return path != null ? path : "/";
            }
        } catch (Exception e) {
            log.warn("Failed to build WebSocket URI: {}", e.getMessage());
        }
        return "/";
    }
}
