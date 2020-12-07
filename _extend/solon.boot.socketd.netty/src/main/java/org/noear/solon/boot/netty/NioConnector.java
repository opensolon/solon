package org.noear.solon.boot.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.Connector;

import java.net.URI;

public class NioConnector implements Connector<Channel> {
    URI uri;

    public NioConnector(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public Channel open(Session session) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new NioChannelInitializer(() -> new NioClientProcessor(session)));

            ChannelFuture channelFuture = bootstrap.connect(uri.getHost(), uri.getPort()).sync();

            return channelFuture.channel();
        } catch (Exception ex) {
            eventLoopGroup.shutdownGracefully();
            throw new RuntimeException(ex);
        }
    }
}
