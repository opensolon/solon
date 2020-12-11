package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.protocol.MessageProtocol;
import org.noear.solon.extend.socketd.protocol.MessageProtocolDefault;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageProtocolManager {
    private static MessageProtocol protocol = MessageProtocolDefault.instance;
    private static SSLContext sslContext;

    public static void setProtocol(MessageProtocol protocol) {
        if (protocol != null) {
            MessageProtocolManager.protocol = protocol;
        }
    }

    public static void setSslContext(SSLContext sslContext){
        MessageProtocolManager.sslContext = sslContext;
    }


    public static ByteBuffer encode(Message message) throws IOException {
        return protocol.encode(message);
    }

    public static Message decode(ByteBuffer buffer) throws IOException{
        return protocol.decode(buffer);
    }
}
