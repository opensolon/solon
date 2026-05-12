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
package org.noear.solon.server.netahttp.http;

import net.hasor.neta.channel.ProtoContext;
import net.hasor.neta.channel.ProtoHandler;
import net.hasor.neta.channel.ProtoStatus;
import net.hasor.neta.channel.data.ProtoRcvQueue;
import net.hasor.neta.channel.data.ProtoSndQueue;
import net.hasor.neta.codec.http.*;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.server.ServerProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于 neta-codec-http 的 HTTP 请求处理器
 *
 * @author noear
 * @since 3.10
 */
public class NetaHttpContextHandler implements ProtoHandler<HttpObject, Object> {
    static final Logger log = LoggerFactory.getLogger(NetaHttpContextHandler.class);

    private final Handler handler;

    public NetaHttpContextHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public ProtoStatus onMessage(ProtoContext context, ProtoRcvQueue<HttpObject> src,
                                 ProtoSndQueue<Object> dst) throws Throwable {
        HttpObject obj;
        while ((obj = src.takeMessage()) != null) {
            if (!(obj instanceof FullHttpRequest)) {
                continue;
            }

            FullHttpRequest httpRequest = (FullHttpRequest) obj;

            try {
                if (httpRequest.isBad()) {
                    sendErrorResponse(context, httpRequest, HttpStatus.BAD_REQUEST,
                            httpRequest.badReason() != null ? httpRequest.badReason() : "Bad Request");
                    continue;
                }

                NetaHttpContext ctx = new NetaHttpContext(httpRequest);
                handleDo(ctx);

                // 提交响应并通过 sendData 发回协议栈 send 路径
                FullHttpResponse resp = ctx.commitResponse();
                resp.streamId(httpRequest.streamId());
                context.sendData(resp);

            } catch (Throwable e) {
                log.warn("HTTP handler error", e);
                sendErrorResponse(context, httpRequest, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            } finally {
                httpRequest.release();
            }
        }

        return ProtoStatus.Next;
    }

    private void sendErrorResponse(ProtoContext context, FullHttpRequest httpRequest,
                                   HttpStatus status, String reason) {
        try {
            byte[] bodyBytes = reason.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            net.hasor.neta.bytebuf.ByteBuf bodyBuf = net.hasor.neta.bytebuf.ByteBuf.wrap(bodyBytes);

            DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, bodyBuf);
            resp.setHeader("Content-Type", "text/plain; charset=utf-8");
            resp.setHeader("Content-Length", String.valueOf(bodyBytes.length));
            resp.streamId(httpRequest.streamId());
            context.sendData(resp);
        } catch (Throwable ex) {
            log.warn("Failed to send error response: {}", ex.getMessage());
        }
    }

    protected void handleDo(NetaHttpContext ctx) {
        try {
            if (Solon.app() != null && ServerProps.output_meta) {
                ctx.headerSet("Solon-Server", "neta-http/" + Solon.version());
            }

            handler.handle(ctx);

        } catch (Throwable e) {
            log.warn("HTTP handler error: " + e.getMessage(), e);
            ctx.status(500);
        }
    }
}
