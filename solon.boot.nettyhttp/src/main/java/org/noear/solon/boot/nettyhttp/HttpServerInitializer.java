package org.noear.solon.boot.nettyhttp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

class HttpServerInitializer  extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new HttpServerCodec());// http 编解码
        pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
        pipeline.addLast("httpAggregator", new HttpObjectAggregator(HttpServerConfig.maxContentLength)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
        pipeline.addLast(new HttpRequestHandler());// 请求处理器

    }
}
