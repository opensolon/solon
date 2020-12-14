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
    private URI uri;
    private boolean autoReconnect;

    public NioConnector(URI uri, boolean autoReconnect) {
        this.uri = uri;
        this.autoReconnect = autoReconnect;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public boolean autoReconnect() {
        return autoReconnect;
    }

    @Override
    public Class<Channel> realType() {
        return Channel.class;
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
