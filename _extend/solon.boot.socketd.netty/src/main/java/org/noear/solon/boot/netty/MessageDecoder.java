package org.noear.solon.boot.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.MessageUtils;

import java.nio.ByteBuffer;
import java.util.List;

class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        int len = byteBuf.readInt();
        if(len > 0) {
            byte[] bytes = new byte[len - Integer.BYTES];
            byteBuf.readBytes(bytes);

            ByteBuffer byteBuffer = ByteBuffer.allocate(len);
            byteBuffer.putInt(len);
            byteBuffer.put(bytes);
            byteBuffer.flip();

            Message message = MessageUtils.decode(byteBuffer);
            if (message != null) {
                out.add(message);
            }

            byteBuffer.compact();
        }
        byteBuf.discardReadBytes();
    }
}
