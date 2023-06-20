package org.noear.solon.boot.websocket.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.noear.solon.Solon;
import org.noear.solon.core.message.Message;

/**
 * @author noear
 * @since 2.3
 */
public class WsServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    /**
     * 客户端发送给服务端的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        //listener.onMessage();
        if (msg instanceof TextWebSocketFrame) {
            String msgTxt = ((TextWebSocketFrame) msg).text();
            Solon.app().listener().onMessage(_SocketServerSession.get(ctx), Message.wrap(msgTxt));

        } else if (msg instanceof BinaryWebSocketFrame) {
            byte[] bytes = ((BinaryWebSocketFrame) msg).content().array();
            Solon.app().listener().onMessage(_SocketServerSession.get(ctx), Message.wrap(bytes));
        }
    }

    /**
     * 客户端连接时候的操作
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //listener.onOpen();
        Solon.app().listener().onOpen(_SocketServerSession.get(ctx));
    }

    /**
     * 客户端掉线时的操作
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //listener.onClose();
        Solon.app().listener().onClose(_SocketServerSession.get(ctx));
    }

    /**
     * 发生异常时执行的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //listener.onError();
        Solon.app().listener().onError(_SocketServerSession.get(ctx), cause);
    }
}