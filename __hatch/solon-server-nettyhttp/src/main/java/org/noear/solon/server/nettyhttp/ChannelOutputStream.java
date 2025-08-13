/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.server.nettyhttp;

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
