package org.noear.solon.net.stomp;

import org.noear.solon.net.websocket.WebSocket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * stomp 处理工具类
 *
 * @author limliu
 * @since 2.7
 */
public abstract class StompUtil {

    public final static MessageCodec msgCodec = new MessageCodecImpl();

    public static void send(WebSocket webSocket, Message message) {
        try {
            webSocket.send(ByteBuffer.wrap(msgCodec.encode(message).getBytes(StandardCharsets.UTF_8)));
        } finally {
            if (!webSocket.isValid()) {
                webSocket.close();
            }
        }
    }
}
