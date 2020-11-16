package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.message.Message;

import org.noear.solon.extend.xsocket.MessageUtils;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;


/**
 * /date/xxx/sss\n...
 *
 * */
public class AioProtocol implements Protocol<Message> {

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

