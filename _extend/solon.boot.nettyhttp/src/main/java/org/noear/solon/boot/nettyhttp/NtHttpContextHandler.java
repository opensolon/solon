package org.noear.solon.boot.nettyhttp;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

class NtHttpContextHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private XApp app = XApp.global();

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

        NtHttpContext context = new NtHttpContext(ctx, req, response);
        context.contentType("text/plain;charset=UTF-8");//默认
        if(XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        try {
            app.handle(context);
        } catch (Throwable ex) {
            ex.printStackTrace();

            context.status(500);
            context.setHandled(true);
            context.output(XUtil.getFullStackTrace(ex));
        }

        try{
            if (context.getHandled() && context.status() != 404) {
                context.commit();
            } else {
                context.status(404);
                context.commit();
            }
        }finally {
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
