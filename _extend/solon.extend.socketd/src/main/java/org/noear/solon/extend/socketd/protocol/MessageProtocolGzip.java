package org.noear.solon.extend.socketd.protocol;

import org.noear.solon.core.message.FrameFlag;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.MessageUtils;
import org.noear.solon.extend.socketd.protocol.util.GzipUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageProtocolGzip implements MessageProtocol {
    @Override
    public ByteBuffer encode(Message message) throws IOException {
        ByteBuffer buffer = MessageProtocolDefault.instance.encode(message);

        byte[] bytes = GzipUtil.compress(buffer.array());

        Message message2 = MessageUtils.wrapContainer(bytes);

        return MessageProtocolDefault.instance.encode(message2);
    }

    @Override
    public Message decode(ByteBuffer buffer) throws IOException {
        Message message = MessageProtocolDefault.instance.decode(buffer);
        if (message.flag() == FrameFlag.container) {
            byte[] bytes = GzipUtil.uncompress(message.body());
            buffer = ByteBuffer.wrap(bytes);

            message = MessageProtocolDefault.instance.decode(buffer);
        }

        return message;
    }
}
