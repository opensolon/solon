package org.noear.solon.socketd.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.noear.solon.Solon;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

public class NioClientProcessor extends SimpleChannelInboundHandler<Message> {
    Session session;
    public NioClientProcessor(Session session){
        this.session = session;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Solon.app().listener().onMessage(session, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        Solon.app().listener().onOpen(session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        Solon.app().listener().onClose(session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Solon.app().listener().onError(session, cause);

        //cause.printStackTrace();
        ctx.close();
    }
}