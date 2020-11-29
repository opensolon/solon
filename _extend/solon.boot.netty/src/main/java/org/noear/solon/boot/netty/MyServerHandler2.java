package org.noear.solon.boot.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.noear.solon.core.message.Message;

import java.util.UUID;

public class MyServerHandler2 extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        //打印出客户端地址
        System.out.println(ctx.channel().remoteAddress() + ", " + msg);
        ctx.writeAndFlush(Message.wrap((UUID.randomUUID() + "").getBytes()));
        //ctx.channel().writeAndFlush("form server: "+ UUID.randomUUID());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}