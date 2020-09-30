package org.noear.solon.api.socket;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SocketMessage {
    /**
     * 消息key
     */
    public String key;
    /**
     * 资源描述
     */
    public String resourceDescriptor;
    /**
     * 消息内容
     */
    public byte[] content;
    /**
     * 消息编码
     */
    public Charset charset = StandardCharsets.UTF_8;

    @Override
    public String toString() {
        if (content == null) {
            return null;
        } else {
            return new String(content, charset);
        }
    }

    /**
     * 打包
     */
    public static SocketMessage wrap(byte[] bytes) {
        return wrap(null, null, bytes);
    }

    /**
     * 打包
     */
    public static SocketMessage wrap(String resourceDescriptor, byte[] bytes) {
        return wrap(UUID.randomUUID().toString(), resourceDescriptor, bytes);
    }

    /**
     * 打包
     */
    public static SocketMessage wrap(String key, String resourceDescriptor, byte[] bytes) {
        SocketMessage msg = new SocketMessage();

        msg.key = key;
        msg.resourceDescriptor = resourceDescriptor;
        msg.content = bytes;

        return msg;
    }
}
