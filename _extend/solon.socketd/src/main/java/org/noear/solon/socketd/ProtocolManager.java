package org.noear.solon.socketd;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.socketd.protocol.MessageProtocol;
import org.noear.solon.socketd.protocol.MessageProtocolBase;

import java.nio.ByteBuffer;

public class ProtocolManager {
    private static MessageProtocol protocol = MessageProtocolBase.instance;

    public static void setProtocol(MessageProtocol protocol) {
        if (protocol != null) {
            ProtocolManager.protocol = protocol;
        }
    }

    public static ByteBuffer encode(Message message) {
        try {
            return protocol.encode(message);
        } catch (Throwable ex) {
            EventBus.push(ex);
            return null;
        }
    }

    public static Message decode(ByteBuffer buffer)  {
        try {
            return protocol.decode(buffer);
        } catch (Throwable ex) {
            EventBus.push(ex);
            return null;
        }
    }
}
