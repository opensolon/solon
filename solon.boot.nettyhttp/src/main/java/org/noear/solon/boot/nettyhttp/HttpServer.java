package org.noear.solon.boot.nettyhttp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

 class HttpServer {
    private int port ;
    private ChannelFuture _server;

    public HttpServer(int port){
        this.port = port;
    }

    public void start() throws Exception{

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            _server = bootstrap.bind(new InetSocketAddress(port)).sync();
        }catch (Throwable ex){
            _server = null;
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    public void stop() throws Throwable {
        if(_server != null) {
            _server.channel().close();
            _server = null;
        }
    }
}
