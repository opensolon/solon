package org.noear.solon.boot.nettyhttp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.net.InetSocketAddress;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.core.handle.Handler;

public final class NettyHttpServer implements ServerLifecycle {

    ChannelFuture _server;

    private ServerBootstrap b;
    private final InetSocketAddress _address;
    private final HttpServerProps _props;
    private final Handler _handler;

    public NettyHttpServer(final InetSocketAddress address,
            final HttpServerProps props,
            final Handler handler) {
        this._address = address;
        this._props = props;
        this._handler = handler;
    }


    @Override
    public void start(final String host, final int port) throws Throwable {
        new Thread(() -> {
            EventLoopGroup boos = new NioEventLoopGroup(_props.getCoreThreads()),
                    work = new NioEventLoopGroup(_props.getMaxThreads(false));
            try {
                b = new ServerBootstrap();
                b.group(boos, work)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                p.addLast(new HttpRequestDecoder());
                                p.addLast(new HttpObjectAggregator(65536));
                                p.addLast(new HttpResponseEncoder());
                                p.addLast(new ChunkedWriteHandler());
                                p.addLast(new HttpServerHandler(_handler));
                            }
                        });
                // 服务器绑定端口监听
                _server = b.bind(port).sync();
                System.out.println("服务端启动成功,端口是:" + port);
                // 监听服务器关闭监听
                _server.channel().closeFuture().await();
                System.out.println("boos = " + boos);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                boos.shutdownGracefully();
                work.shutdownGracefully();
            }
        }).start();
    }

    @Override
    public void stop() {
        if (_server == null) {
            return;
        }

        _server.channel().close();
        _server = null;
    }

    public boolean isSecure() {
        return false;
    }
}
