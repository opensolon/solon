package org.noear.solon.socketd.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ConnectorBase;

import java.net.URI;

class NioConnector extends ConnectorBase<Channel> {


    public NioConnector(URI uri, boolean autoReconnect) {
        super(uri, autoReconnect);
    }

    @Override
   public Class<Channel> driveType() {
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

           ChannelFuture channelFuture = bootstrap.connect(uri().getHost(), uri().getPort()).sync();

           return channelFuture.channel();
       } catch (Exception ex) {
           eventLoopGroup.shutdownGracefully();
           throw new RuntimeException(ex);
       }
   }
}
