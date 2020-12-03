package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Message;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class MessageWrapper {

    /**
     * 打包
     */
    public static Message wrap(byte[] body) {
        return wrap(null, null, body);
    }


    /**
     * 打包
     */
    public static Message wrap(String resourceDescriptor, String header, byte[] body) {
        return new Message(0, UUID.randomUUID().toString(), resourceDescriptor, header, body);
    }


    /**
     * 打包
     */
    public static Message wrap(String key, String resourceDescriptor, String header, byte[] body) {
        return new Message(0, key, resourceDescriptor, header, body);
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


    /**
     * 包装心跳包
     */
    public static Message wrapHeartbeat() {
        return new Message(-2, UUID.randomUUID().toString(), "", null, null);
    }

    /**
     * 包装握手包
     */
    public static Message wrapHandshake(String header, byte[] body) {
        return new Message(-1, UUID.randomUUID().toString(), "", header, body);
    }

    public static Message wrapHandshake(String header) {
        return wrapHandshake(header, null);
    }

    /**
     * 包装响应包
     */
    public static Message wrapResponse(Message request, String header, byte[] body) {
        return new Message(1, request.key(), request.resourceDescriptor(), header, body);
    }

    public static Message wrapResponse(Message request, byte[] body) {
        return wrapResponse(request, body);
    }
}
