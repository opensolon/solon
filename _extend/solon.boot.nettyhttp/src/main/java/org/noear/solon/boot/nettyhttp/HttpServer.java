package org.noear.solon.boot.nettyhttp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

class HttpServer {
    private int port ;
    private ChannelFuture _server;

    public HttpServer(int port){
        this.port = port;
    }

    public void start() throws Exception{

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
//                    .handler(new LoggingHandler(LogLevel.ERROR))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            _server = bootstrap.bind(new InetSocketAddress(port)).sync();

            //这句不能去掉
            //
            new Thread(_server.channel().closeFuture()::syncUninterruptibly).start();
        }catch (Throwable ex){
            ex.printStackTrace();
            _server = null;
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public void stop() throws Throwable {
        if(_server != null) {
            _server.channel().close();
            _server = null;
        }
    }
}
