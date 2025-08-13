/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.server.websocket.netty;

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

import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.prop.ServerSslProps;
import org.noear.solon.server.prop.impl.WebSocketServerProps;
import org.noear.solon.server.ssl.SslContextFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * @author noear
 * @since 2.3
 */
public class NettyWsServer implements ServerLifecycle {
    private ChannelFuture _server;
    private final WebSocketServerProps _props;

    private ServerSslProps sslProps;
    private SSLContext sslContext;

    protected boolean supportSsl() {
        if (sslProps == null) {
            sslProps = ServerSslProps.of(ServerConstants.SIGNAL_WEBSOCKET);
        }

        return sslProps.isEnable() && sslProps.getSslKeyStore() != null;
    }

    public NettyWsServer(WebSocketServerProps props){
        _props = props;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        EventLoopGroup bossGroup = new NioEventLoopGroup(_props.getCoreThreads());
        EventLoopGroup workGroup = new NioEventLoopGroup(_props.getMaxThreads(false));

        try {
            if(supportSsl()){
                sslContext = SslContextFactory.create(sslProps);
            }

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            if(sslContext != null) {
                                SSLEngine engine = sslContext.createSSLEngine();
                                engine.setUseClientMode(false);
                                engine.setNeedClientAuth(true);
                                pipeline.addFirst(new SslHandler(engine));
                            }

                            //将请求和应答消息编码或解码为HTTP消息
                            pipeline.addLast(new HttpServerCodec());
                            //将HTTP消息的多个部分组合成一条完整的HTTP消息
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            //向客户端发送HTML5文件，主要用于支持浏览器和服务端进行WebSocket通信
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new NettyWsServerHandler(_props));
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
