package org.noear.solon.socketd.client.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;
import org.noear.solon.core.message.Message;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.function.Supplier;

public class NioChannelInitializer extends ChannelInitializer<SocketChannel> {
    Supplier<SimpleChannelInboundHandler<Message>> processor;
    SSLContext sslContext;
    boolean clientMode;

    public NioChannelInitializer(SSLContext sslContext, boolean clientMode, Supplier<SimpleChannelInboundHandler<Message>> processor) {
        this.processor = processor;
        this.sslContext = sslContext;
        this.clientMode = clientMode;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //pipeline.addLast(new IdleStateHandler())

        if (sslContext != null) {
            SSLEngine engine = sslContext.createSSLEngine();
            if (clientMode == false) {
                engine.setUseClientMode(false);
                engine.setNeedClientAuth(true);
            }
            pipeline.addFirst(new SslHandler(engine));
        }

        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 0));
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new MessageEncoder());
        pipeline.addLast(processor.get());
    }
}
