package org.noear.solon.extend.socketd;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * SocketD 消息包，提供编码解码示例
 *
 * @author noear
 * @since 1.0
 * */
public class MessageUtils {

    //
    // encode & decode
    //

    /**
     * 编码
     */
    public static ByteBuffer encode(Message message) {
        try {
            return ProtocolManager.encode(message);
        } catch (Exception ex) {
            EventBus.push(ex);
            return null;
        }
    }

    /**
     * 解码
     */
    public static Message decode(ByteBuffer buffer) {
        try {
            return ProtocolManager.decode(buffer);
        } catch (Exception ex) {
            EventBus.push(ex);
            return null;
        }
    }


    //
    // warp
    //


    /**
     * 打包
     */
    public static Message wrap(byte[] body) {
        return wrap(null, null, body);
    }

    public static Message wrap(String body) {
        return wrap(body.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 打包
     */
    public static Message wrap(String resourceDescriptor, String header, byte[] body) {
        return new Message(MessageFlag.message, Utils.guid(), resourceDescriptor, header, body);
    }


    /**
     * 打包
     */
    public static Message wrap(String key, String resourceDescriptor, String header, byte[] body) {
        return new Message(MessageFlag.message, key, resourceDescriptor, header, body);
    }


    //
    //属性打包
    //

    public static Message wrapJson(String resourceDescriptor, byte[] body) {
        return wrap(resourceDescriptor, "Content-Type=application/json", body);
    }

    public static Message wrapJson(String resourceDescriptor, String body) {
        try {
            return wrapJson(resourceDescriptor, body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Message wrapContainer(byte[] body) {
        return new Message(MessageFlag.container,  body);
    }

    /**
     * 包装心跳包
     */
    public static Message wrapHeartbeat() {
        return new Message(MessageFlag.heartbeat, UUID.randomUUID().toString(), null);
    }


    /**
     * 包装握手包（只支持用头）
     */
    public static Message wrapHandshake(String header, byte[] body) {
        return new Message(MessageFlag.handshake, UUID.randomUUID().toString(), "", header, body);
    }

    public static Message wrapHandshake(String header) {
        return wrapHandshake(header, null);
    }

    /**
     * 包装响应包
     */
    public static Message wrapResponse(Message request, byte[] body) {
        return new Message(MessageFlag.response, request.key(), body);
    }

    public static Message wrapResponse(Message request, String body) {
        return new Message(MessageFlag.response, request.key(), body.getBytes(StandardCharsets.UTF_8));
    }
}