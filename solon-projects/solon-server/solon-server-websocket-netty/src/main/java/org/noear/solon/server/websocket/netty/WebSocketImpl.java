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
package org.noear.solon.server.websocket.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.WebSocketTimeoutBase;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketImpl extends WebSocketTimeoutBase {
    private ChannelHandlerContext real;

    public WebSocketImpl(ChannelHandlerContext real) {
        this.real = real;
        String uri = DecodeUtils.rinseUri(real.attr(NettyWsServerHandler.ResourceDescriptorKey).get());

        this.init(URI.create(uri));
    }

    @Override
    public boolean isValid() {
        return isClosed() == false && real.channel().isOpen();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) real.channel().remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) real.channel().localAddress();
    }


    @Override
    public Future<Void> send(String text) {
        try {
            return real.writeAndFlush(new TextWebSocketFrame(text));
        } finally {
            onSend();
        }
    }

    @Override
    public Future<Void> send(ByteBuffer binary) {
        try {
            return real.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(binary)));
        } finally {
            onSend();
        }
    }

    @Override
    public void close() {
        super.close();

        if (real.channel().isOpen()) {
            RunUtil.runAndTry(real::close);
        }
    }
}