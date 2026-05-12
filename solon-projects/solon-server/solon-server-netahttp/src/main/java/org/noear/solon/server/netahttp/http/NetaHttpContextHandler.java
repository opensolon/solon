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

import net.hasor.neta.channel.*;
import net.hasor.neta.channel.data.ProtoRcvQueue;
import net.hasor.neta.channel.data.ProtoSndQueue;
import net.hasor.neta.codec.http.*;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.server.ServerProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于 neta-codec-http 的 HTTP 请求处理器
 * <p>
 * 作为 ProtoDuplexer 接入 neta 协议栈，接收 FullHttpRequest，
 * 转为 Solon Context 处理后，返回 FullHttpResponse。
 *
 * @author noear
 * @since 3.10
 */
public class NetaHttpContextHandler implements ProtoDuplexer<HttpObject, HttpObject, Object, Object> {
    static final Logger log = LoggerFactory.getLogger(NetaHttpContextHandler.class);

    private final Handler handler;

    public NetaHttpContextHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public ProtoStatus onMessage(ProtoContext context, boolean isRcv,
                                 ProtoRcvQueue<HttpObject> rcvUp, ProtoSndQueue<HttpObject> rcvDown,
                                 ProtoRcvQueue<Object> sndUp, ProtoSndQueue<Object> sndDown) throws Throwable {
        if (!isRcv) {
            return ProtoStatus.Next;
        }

        HttpObject obj;
        while ((obj = rcvUp.takeMessage()) != null) {
            if (!(obj instanceof FullHttpRequest)) {
                obj.release();
                continue;
            }

            FullHttpRequest httpRequest = (FullHttpRequest) obj;

            if (httpRequest.isBad()) {
                // 请求解析错误，返回 400
                FullHttpResponse badResp = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpStatus.BAD_REQUEST);
                badResp.addHeader("Content-Length", "0");
                rcvDown.offerMessage(badResp);
                httpRequest.release();
                continue;
            }

            NetaHttpContext ctx = new NetaHttpContext(httpRequest);
            handleDo(ctx);

            // 提交响应并通过协议栈发回
            FullHttpResponse resp = ctx.commitResponse();
            rcvDown.offerMessage(resp);
            httpRequest.release();
        }

        return ProtoStatus.Next;
    }

    protected void handleDo(NetaHttpContext ctx) {
        try {
            if (ServerProps.output_meta) {
                ctx.headerSet("Solon-Server", "neta-http/" + org.noear.solon.Solon.version());
            }

            handler.handle(ctx);

        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
            ctx.status(500);
        }
    }
}
