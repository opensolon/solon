package org.noear.solon.boot.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.prop.impl.WebSocketServerProps;

/**
 * @author noear
 * @since 2.3
 */
public class WsServer implements ServerLifecycle {
    ChannelFuture _server;
    WebSocketServerProps _props;
    public WsServer(WebSocketServerProps props){
        _props = props;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        EventLoopGroup bossGroup = new NioEventLoopGroup(_props.getCoreThreads());
        EventLoopGroup workerGroup = new NioEventLoopGroup(_props.getMaxThreads(false));

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec()); // HTTP 协议解析，用于握手阶段
                            pipeline.addLast(new HttpObjectAggregator(65536)); // HTTP 协议解析，用于握手阶段
                            pipeline.addLast(new WebSocketServerCompressionHandler()); // WebSocket 数据压缩扩展
                            pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true)); // WebSocket 握手、控制帧处理
                            pipeline.addLast(new WsServerHandler());
                        }
                    });
            if (Utils.isEmpty(host)) {
                _server = bootstrap.bind(port).sync();
            } else {
                _server = bootstrap.bind(host, port).sync();
            }

            _server.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.channel().close();
        _server = null;
    }
}
