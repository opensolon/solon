package org.noear.solon.socketd.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.noear.solon.core.message.Message;
import org.noear.solon.socketd.ProtocolManager;

import java.nio.ByteBuffer;

class MessageEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        if (message != null) {
            ByteBuffer buf = ProtocolManager.encode(message);
            byteBuf.writeBytes(buf.array());
        }
    }
}
