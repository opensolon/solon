package org.noear.solon.boot.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.noear.solon.core.message.Message;

class NioChannelInitializer extends ChannelInitializer<SocketChannel> {
    SimpleChannelInboundHandler<Message> processor;
    NioChannelInitializer(SimpleChannelInboundHandler<Message> processor){
        this.processor = processor;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());
        pipeline.addLast(processor);
    }
}
