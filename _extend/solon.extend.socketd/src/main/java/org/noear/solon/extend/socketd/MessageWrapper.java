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


    //
    //属性打包
    //

    public static Message wrapJson(String resourceDescriptor, byte[] body) {
        return wrap(resourceDescriptor, "Content-Type=application/json", body);
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
}
