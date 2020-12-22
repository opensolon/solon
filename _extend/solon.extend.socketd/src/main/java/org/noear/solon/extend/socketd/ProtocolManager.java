package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.protocol.MessageProtocol;
import org.noear.solon.extend.socketd.protocol.MessageProtocolBase;

import java.nio.ByteBuffer;

public class ProtocolManager {
    private static MessageProtocol protocol = MessageProtocolBase.instance;

    public static void setProtocol(MessageProtocol protocol) {
        if (protocol != null) {
            ProtocolManager.protocol = protocol;
        }
    }

    public static ByteBuffer encode(Message message) throws Exception {
        return protocol.encode(message);
    }

    public static Message decode(ByteBuffer buffer) throws Exception{
        return protocol.decode(buffer);
    }
}
