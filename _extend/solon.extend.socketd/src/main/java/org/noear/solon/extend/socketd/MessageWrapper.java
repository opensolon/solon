package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Message;

import java.util.UUID;

public class MessageWrapper {

    /**
     * 打包
     */
    public static Message wrap(byte[] body) {
        return wrap(null, null, body);
    }

    public static Message wrapJson(String resourceDescriptor, byte[] body) {
        return wrap(resourceDescriptor, "Content-Type=application/json", body);
    }


    /**
     * 打包
     */
    public static Message wrap(String resourceDescriptor, String header, byte[] body) {
        return wrap(0, UUID.randomUUID().toString(), resourceDescriptor, header, body);
    }


    /**
     * 打包
     */
    public static Message wrap(String key, String resourceDescriptor, String header, byte[] body) {
        return wrap(0, key, resourceDescriptor, header, body);
    }

    /**
     * 打包
     */
    public static Message wrap(int flag, String key, String resourceDescriptor, String header, byte[] body) {
        return new Message(flag, key, resourceDescriptor, header, body);
    }
}
