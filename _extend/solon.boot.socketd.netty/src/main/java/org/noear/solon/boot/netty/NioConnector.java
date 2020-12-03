package org.noear.solon.boot.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.noear.solon.core.message.Session;

public class NioConnector {
    private String host;
    private int port;

    public NioConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Channel start(Session session) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new NioChannelInitializer(() -> new NioClientProcessor(session)));

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

            return channelFuture.channel();
        } catch (Exception ex) {
            eventLoopGroup.shutdownGracefully();
            throw new RuntimeException(ex);
        }
    }
}
