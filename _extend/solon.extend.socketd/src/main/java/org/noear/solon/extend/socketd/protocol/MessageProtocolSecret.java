package org.noear.solon.extend.socketd.protocol;

import org.noear.solon.core.message.MessageFlag;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.MessageUtils;

import java.nio.ByteBuffer;

/**
 * 安全协议
 *
 * @author noear
 * @since 1.2
 * */
public abstract class MessageProtocolSecret implements MessageProtocol {
    protected MessageProtocol baseProtocol = MessageProtocolBase.instance;

    public MessageProtocolSecret() {

    }

    public MessageProtocolSecret(MessageProtocol baseProtocol) {
        this.baseProtocol = baseProtocol;
    }


    public abstract byte[] encrypt(byte[] bytes) throws Exception;

    public abstract byte[] decrypt(byte[] bytes) throws Exception;

    @Override
    public ByteBuffer encode(Message message) throws Exception {
        ByteBuffer buffer = baseProtocol.encode(message);


        byte[] bytes = encrypt(buffer.array());
        message = MessageUtils.wrapContainer(bytes);

        return baseProtocol.encode(message);
    }

    @Override
    public Message decode(ByteBuffer buffer) throws Exception {
        Message message = baseProtocol.decode(buffer);

        if (message.flag() == MessageFlag.container) {
            byte[] bytes = decrypt(message.body());
            buffer = ByteBuffer.wrap(bytes);

            message = baseProtocol.decode(buffer);
        }

        return message;
    }
}
