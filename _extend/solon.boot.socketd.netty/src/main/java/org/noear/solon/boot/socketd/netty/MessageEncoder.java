package org.noear.solon.boot.socketd.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.ProtocolManager;

import java.nio.ByteBuffer;

class MessageEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        if (message != null) {
            ByteBuffer buffer = ProtocolManager.encode(message);

            if (buffer != null) {
                byteBuf.writeBytes(buffer.array());
            }
        }
    }
}
