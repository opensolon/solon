package org.noear.solon.socketd.protocol;

import org.noear.solon.core.message.MessageFlag;
import org.noear.solon.core.message.Message;
import org.noear.solon.socketd.protocol.util.GzipUtil;

import java.nio.ByteBuffer;

/**
 * 安全协议
 *
 * @author noear
 * @since 1.2
 * */
public class MessageProtocolCompress implements MessageProtocol {
    protected MessageProtocol baseProtocol = MessageProtocolBase.instance;
    protected int allowCompressSize = 1024;

    public MessageProtocolCompress() {

    }

    public MessageProtocolCompress(int allowCompressSize) {
        this.allowCompressSize = allowCompressSize;
    }

    public MessageProtocolCompress(MessageProtocol baseProtocol) {
        this.baseProtocol = baseProtocol;
    }

    public MessageProtocolCompress(int allowCompressSize, MessageProtocol baseProtocol) {
        this.baseProtocol = baseProtocol;
        this.allowCompressSize = allowCompressSize;
    }

    /**
     * 是否充许压缩
     */
    public boolean allowCompress(int byteSize) {
        return (byteSize > allowCompressSize);
    }

    /**
     * 压缩
     */
    public byte[] compress(byte[] bytes) throws Exception {
        return GzipUtil.compress(bytes);
    }

    /**
     * 解压
     */
    public byte[] uncompress(byte[] bytes) throws Exception {
        return GzipUtil.uncompress(bytes);
    }

    @Override
    public ByteBuffer encode(Message message) throws Exception {
        ByteBuffer buffer = baseProtocol.encode(message);

        if (allowCompress(buffer.array().length)) {
            byte[] bytes = compress(buffer.array());
            message = Message.wrapContainer(bytes);

            buffer = baseProtocol.encode(message);
        }

        return buffer;
    }

    @Override
    public Message decode(ByteBuffer buffer) throws Exception {
        Message message = baseProtocol.decode(buffer);

        if (message.flag() == MessageFlag.container) {
            byte[] bytes = uncompress(message.body());
            buffer = ByteBuffer.wrap(bytes);

            message = baseProtocol.decode(buffer);
        }

        return message;
    }
}
