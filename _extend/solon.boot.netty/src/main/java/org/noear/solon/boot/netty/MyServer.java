package org.noear.solon.boot.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MyServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wokerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //在服务器端的handler()方法表示对bossGroup起作用，而childHandler表示对wokerGroup起作用
            serverBootstrap.group(bossGroup, wokerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializerImpl(new MyServerHandler2()));

            ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }
    }
}
