package org.noear.solon.boot.nettyhttp;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.noear.solon.XApp;
import org.noear.solon.core.XMonitor;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

class NtHttpContextHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        //100 Continue
        if (is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }

        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK);

        try {
            HandleDo(ctx, req, response);
        } catch (Throwable ex) {

            XMonitor.sendError(null, ex);

            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void HandleDo(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) throws IOException {
        NtHttpContext context = new NtHttpContext(ctx, req, res);

        context.contentType("text/plain;charset=UTF-8");//默认
        if (XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        XApp.global().tryHandle(context);

        if (context.getHandled() && context.status() != 404) {
            context.commit();
        } else {
            context.status(404);
            context.commit();
        }
    }
}
