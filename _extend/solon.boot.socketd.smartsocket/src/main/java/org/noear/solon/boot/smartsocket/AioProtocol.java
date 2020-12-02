package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Message;

import org.noear.solon.extend.socketd.MessageUtils;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;


/**
 * /date/xxx/sss\n...
 *
 * */
class AioProtocol implements Protocol<Message> {
    public static final AioProtocol instance = new AioProtocol();

    @Override
    public Message decode(ByteBuffer buffer, AioSession session) {

        if (buffer.remaining() < Integer.BYTES) {
            return null;
        }
        buffer.mark();
        int length = buffer.getInt() - 4;
        if (length > buffer.remaining()) {
            buffer.reset();
            return null;
        }

        buffer.reset();//内部会重新开始读
        Message tmp = MessageUtils.decode(buffer);

        if (tmp == null) {
            buffer.reset();
        }

        return tmp;
    }
}

