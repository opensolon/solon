package org.noear.solon.boot.nettyhttp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.io.IOException;
import java.io.OutputStream;

public class ChannelOutputStream extends OutputStream {

    private final Channel channel;
    private final FullHttpResponse response;

    public ChannelOutputStream(Channel channel, FullHttpResponse response) {
        this.channel = channel;
        this.response = response;
    }

    @Override
    public void write(int b) throws IOException {
        channel.writeAndFlush((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] data = new byte[len];
        System.arraycopy(b, off, data, 0, len);
        response.content().writeBytes(data);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        channel.writeAndFlush(data);
    }

    @Override
    public void flush() throws IOException {
        // 在Netty中，数据已经被立即发送，因此这里不需要额外的操作
    }

    @Override
    public void close() throws IOException {
        // 关闭Channel
        channel.close();
    }
}
