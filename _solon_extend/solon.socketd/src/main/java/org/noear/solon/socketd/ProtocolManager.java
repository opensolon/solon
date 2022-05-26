package org.noear.solon.socketd;

import org.noear.solon.core.message.Message;
import org.noear.solon.socketd.protocol.MessageProtocol;
import org.noear.solon.socketd.protocol.MessageProtocolBase;

import java.nio.ByteBuffer;

/**
 * 消息协议管理者
 *
 * @author noear
 * @since 1.2
 * */
public class ProtocolManager {
    private static MessageProtocol protocol = MessageProtocolBase.instance;

    /**
     * 设置消息协议
     */
    public static void setProtocol(MessageProtocol protocol) {
        if (protocol != null) {
            ProtocolManager.protocol = protocol;
        }
    }

    /**
     * 编码
     *
     * @param message 消息
     */
    public static ByteBuffer encode(Message message) throws IllegalArgumentException {
        try {
            return protocol.encode(message);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 解码
     *
     * @param buffer 缓冲
     */
    public static Message decode(ByteBuffer buffer) throws IllegalArgumentException {
        try {
            return protocol.decode(buffer);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }
}
