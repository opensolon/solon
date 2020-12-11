package org.noear.solon.extend.socketd.protocol;

import org.noear.solon.core.message.Message;

import java.nio.ByteBuffer;

public class MessageProtocolManager {
    private static MessageProtocol protocol = new MessageProtocolImpl();

    public static void setProtocol(MessageProtocol protocol) {
        MessageProtocolManager.protocol = protocol;
    }


    public static ByteBuffer encode(Message message){
        return protocol.encode(message);
    }
    public static Message decode(ByteBuffer buffer){
        return protocol.decode(buffer);
    }
}
