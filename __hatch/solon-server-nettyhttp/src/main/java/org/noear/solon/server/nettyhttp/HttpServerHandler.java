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
package org.noear.solon.server.nettyhttp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.noear.solon.server.ServerProps;
import org.noear.solon.core.handle.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    static final Logger log = LoggerFactory.getLogger(HttpServerHandler.class);

    private final Handler _handler;

    public HttpServerHandler(final Handler handler) {
        this._handler = handler;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx,
            final FullHttpRequest request)
            throws Exception {

        final NettyHttpContext context = new NettyHttpContext(
                ctx,
                request,
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));

        try {
            context.contentType(MimeType.TEXT_PLAIN_UTF8_VALUE);

            if (ServerProps.output_meta) {
                context.headerSet("Solon-Server", XPluginImp.solon_server_ver());
            }

            _handler.handle(context);
//
//            if (context.innerIsAsync()) {
//                //如果启用了异步?
//                context.asyncAwait();
//            } else {
//                context.innerCommit();
//            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            context.sendError();
        }


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生异常时的处理逻辑
        ctx.close();
    }
}