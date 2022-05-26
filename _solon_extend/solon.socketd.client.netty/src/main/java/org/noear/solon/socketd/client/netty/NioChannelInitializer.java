package org.noear.solon.socketd.client.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.noear.solon.core.message.Message;

import java.util.function.Supplier;

public class NioChannelInitializer extends ChannelInitializer<SocketChannel> {
    Supplier<SimpleChannelInboundHandler<Message>> processor;
    public NioChannelInitializer(Supplier<SimpleChannelInboundHandler<Message>> processor){
        this.processor = processor;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //pipeline.addLast(new IdleStateHandler())

        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4,-4,0));
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());
        pipeline.addLast(processor.get());
    }
}
