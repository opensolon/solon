package org.noear.solon.boot.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.xsocket.MessageUtils;

import java.util.List;

public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        if (message != null) {
            byte[] bytes = MessageUtils.encode(message).array();
            byteBuf.writeBytes(bytes);
        }
    }
}
