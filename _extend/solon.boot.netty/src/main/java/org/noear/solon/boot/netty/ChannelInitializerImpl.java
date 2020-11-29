package org.noear.solon.boot.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class ChannelInitializerImpl extends ChannelInitializer<SocketChannel> {
    private SimpleChannelInboundHandler handler;

    public ChannelInitializerImpl(SimpleChannelInboundHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new MessageDecoder());

        pipeline.addLast(new LengthFieldPrepender(4));
//        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new MessageEncoder());
        pipeline.addLast(handler);


    }
}
