package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Message 包装类
 *
 * @author noear
 * @since 1.0
 * */
public class MessageUtils {
    public static String guid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 打包
     */
    public static Message wrap(byte[] body) {
        return new Message(MessageFlag.message, guid(), body);
    }

    public static Message wrap(String body) {
        return wrap(body.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 打包
     */
    public static Message wrap(String resourceDescriptor, String header, byte[] body) {
        return new Message(MessageFlag.message, guid(), resourceDescriptor, header, body);
    }

    public static Message wrap(String resourceDescriptor, String header, String body) {
        return wrap(resourceDescriptor, header, body.getBytes(StandardCharsets.UTF_8));
    }


    //
    //属性打包
    //

    /**
     * 包装容器包（用于二次编码，如加密、压缩...）
     */
    public static Message wrapContainer(byte[] body) {
        return new Message(MessageFlag.container, body);
    }

    /**
     * 包装心跳包
     */
    public static Message wrapHeartbeat() {
        return new Message(MessageFlag.heartbeat, guid(), null);
    }


    /**
     * 包装握手包（只支持用头）
     */
    public static Message wrapHandshake(String header, byte[] body) {
        return new Message(MessageFlag.handshake, guid(), "", header, body);
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

    /**
     * 包装请求包（一般用不到）
     */
    public static Message wrapRequest(String resourceDescriptor, String header, byte[] body) {
        return new Message(MessageFlag.request, guid(), resourceDescriptor, header, body);
    }

    public static Message wrapRequest(String resourceDescriptor, String header, String body) {
        return wrapRequest(resourceDescriptor, header, body.getBytes(StandardCharsets.UTF_8));
    }
}