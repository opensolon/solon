package org.noear.solon.boot.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.noear.solon.core.message.Message;

import java.time.LocalDateTime;


public class MyClientHandler2 extends SimpleChannelInboundHandler<Message>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        //服务端的远程地址
        System.out.println(ctx.channel().remoteAddress());
        System.out.println("client output: "+msg);
        ctx.writeAndFlush(Message.wrap(("from client: "+ LocalDateTime.now()).getBytes()));
    }

    /**
     * 当服务器端与客户端进行建立连接的时候会触发，如果没有触发读写操作，则客户端和客户端之间不会进行数据通信，也就是channelRead0不会执行，
     * 当通道连接的时候，触发channelActive方法向服务端发送数据触发服务器端的handler的channelRead0回调，然后
     * 服务端向客户端发送数据触发客户端的channelRead0，依次触发。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Message.wrap("来自与客户端的问题!".getBytes()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
