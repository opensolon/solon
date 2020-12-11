package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.protocol.MessageProtocol;
import org.noear.solon.extend.socketd.protocol.MessageProtocolDefault;

import java.nio.ByteBuffer;

public class MessageProtocolManager {
    private static MessageProtocol protocol = new MessageProtocolDefault();

    public static void setProtocol(MessageProtocol protocol) {
        if (protocol != null) {
            MessageProtocolManager.protocol = protocol;
        }
    }


    public static ByteBuffer encode(Message message) {
        return protocol.encode(message);
    }

    public static Message decode(ByteBuffer buffer) {
        return protocol.decode(buffer);
    }
}
