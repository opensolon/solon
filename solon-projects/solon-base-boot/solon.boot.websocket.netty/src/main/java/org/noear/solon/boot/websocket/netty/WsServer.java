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

import io.netty.handler.stream.ChunkedWriteHandler;
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
        EventLoopGroup workGroup = new NioEventLoopGroup(_props.getMaxThreads(false));

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //将请求和应答消息编码或解码为HTTP消息
                            pipeline.addLast(new HttpServerCodec());
                            //将HTTP消息的多个部分组合成一条完整的HTTP消息
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            //向客户端发送HTML5文件，主要用于支持浏览器和服务端进行WebSocket通信
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new WsServerHandler());
                        }
                    });
            if (Utils.isEmpty(host)) {
                _server = bootstrap.bind(port).await();
            } else {
                _server = bootstrap.bind(host, port).await();
            }
        } catch (RuntimeException e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

            throw e;
        } catch (Throwable e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

            throw new IllegalStateException(e);
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
