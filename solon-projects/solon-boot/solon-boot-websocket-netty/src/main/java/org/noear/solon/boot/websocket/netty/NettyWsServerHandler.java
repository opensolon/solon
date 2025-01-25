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
package org.noear.solon.boot.websocket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.noear.solon.Utils;
import org.noear.solon.boot.prop.impl.WebSocketServerProps;
import org.noear.solon.boot.web.DecodeUtils;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.3
 */
public class NettyWsServerHandler extends SimpleChannelInboundHandler<Object> {
    static final Logger log = LoggerFactory.getLogger(NettyWsServerHandler.class);
    public static final AttributeKey<String> ResourceDescriptorKey = AttributeKey.valueOf("ResourceDescriptor");
    public static final AttributeKey<WebSocketServerHandshaker> HandshakerKey = AttributeKey.valueOf("Handshaker");
    public static final AttributeKey<WebSocketImpl> SessionKey = AttributeKey.valueOf("Session");

    static final WebSocketServerProps wsProps = WebSocketServerProps.getInstance();

    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断请求是HTTP请求还是WebSocket请求
        if (msg instanceof FullHttpRequest) {
            //处理WebSocket握手请求
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            //处理WebSocket请求
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        //先判断解码是否成功，然后判断是不是请求建立WebSocket连接

        //如果HTTP解码失败，返回HTTP异常
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //生成 ResourceDescriptor
        String url = "ws://" + req.headers().get(HttpHeaderNames.HOST) + DecodeUtils.rinseUri(req.uri());

        //构造握手工厂创建握手处理类 WebSocketServerHandshaker，来构造握手响应返回给客户端
        WebSocketServerHandshakerFactory wsFactory = null;

        //添加子协议支持
        String path = URI.create(url).getPath();
        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            String protocols = subProtocolCapable.getSubProtocols(req.headers().getAll(SubProtocolCapable.SEC_WEBSOCKET_PROTOCOL));

            if (Utils.isNotEmpty(protocols)) {
                wsFactory = new WebSocketServerHandshakerFactory(url, protocols, false);
            }
        }

        if (wsFactory == null) {
            wsFactory = new WebSocketServerHandshakerFactory(url, null, false);
        }

        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
            ctx.attr(HandshakerKey).set(handshaker);
            ctx.attr(ResourceDescriptorKey).set(DecodeUtils.rinseUri(req.uri()));

            //listener.onOpen();
            WebSocketImpl webSocket = new WebSocketImpl(ctx);
            ctx.attr(SessionKey).set(webSocket);
            webSocketRouter.getListener().onOpen(webSocket);

            //设置闲置超时
            if (wsProps.getIdleTimeout() > 0) {
                webSocket.setIdleTimeout(wsProps.getIdleTimeout());
            }
        }
    }


    //如果接收到的消息是已经解码的WebSocketFrame消息
    public void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        //先对控制帧进行判断
        //判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker handshaker = ctx.attr(HandshakerKey).get();
            if (handshaker != null) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }

        //判断是否是维持链路的Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));

            WebSocketImpl webSocket = ctx.attr(SessionKey).get();
            if (webSocket != null) {
                webSocket.onReceive();
            }
            webSocketRouter.getListener().onPing(webSocket);
            return;
        }

        if (frame instanceof PongWebSocketFrame) {
            WebSocketImpl webSocket = ctx.attr(SessionKey).get();
            if (webSocket != null) {
                webSocket.onReceive();
            }

            webSocketRouter.getListener().onPong(webSocket);
            return;
        }

        if (frame instanceof TextWebSocketFrame) {
            //listener.onMessage();
            WebSocketImpl webSocket = ctx.attr(SessionKey).get();
            webSocket.onReceive();

            String msgTxt = ((TextWebSocketFrame) frame).text();

            webSocketRouter.getListener().onMessage(webSocket, msgTxt);
            return;
        }

        if (frame instanceof BinaryWebSocketFrame) {
            //listener.onMessage();
            WebSocketImpl webSocket = ctx.attr(SessionKey).get();
            webSocket.onReceive();

            ByteBuffer msgBuf = frame.content().nioBuffer();

            webSocketRouter.getListener().onMessage(webSocket, msgBuf);
            return;
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp) {
        if (resp.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(resp, resp.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(resp);
        if (!HttpUtil.isKeepAlive(resp) || resp.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 客户端掉线时的操作
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //listener.onClose();
        WebSocketImpl webSocket = ctx.attr(SessionKey).get();
        if (webSocket.isClosed()) {
            return;
        } else {
            RunUtil.runAndTry(webSocket::close);
        }
        webSocketRouter.getListener().onClose(webSocket);
    }

    /**
     * 发生异常时执行的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //listener.onError();
        try {
            WebSocket webSocket = ctx.attr(SessionKey).get();
            webSocketRouter.getListener().onError(webSocket, cause);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }

        RunUtil.runAndTry(ctx::close);
    }
}