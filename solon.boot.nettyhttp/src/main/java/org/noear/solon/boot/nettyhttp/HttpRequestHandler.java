package org.noear.solon.boot.nettyhttp;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.noear.solon.XApp;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

 class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
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

         NettyHttpContext context = new NettyHttpContext(req,response);
         context.contentType("text/plain;charset=UTF-8");//默认

         try {
             app.handle(context);
         }catch (Throwable ex){
             ex.printStackTrace();
         }

         try{
             context.commit();
         }catch (Exception ex){
             ex.printStackTrace();
         }

        // 将html write到客户端
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
