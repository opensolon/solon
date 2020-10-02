package org.noear.solonx.socket.api;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * XSocket 消息包
 *
 * @author noear
 * @since 1.0
 * */
public class XSocketMessage {
    /**
     * 1.消息key
     */
    private String key;

    public String key() {
        return key;
    }

    /**
     * 2.资源描述
     */
    private String resourceDescriptor;

    public String resourceDescriptor() {
        return resourceDescriptor;
    }

    /**
     * 3.消息内容
     */
    private byte[] content;

    public byte[] content() {
        return content;
    }

    @Override
    public String toString() {
        if (content == null) {
            return null;
        } else {
            return new String(content, charset);
        }
    }

    /**
     * 消息编码
     */
    private Charset charset = StandardCharsets.UTF_8;
    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /////////////////////
    private boolean _handled;

    public void setHandled(boolean handled) {
        _handled = handled;
    }

    public boolean getHandled() {
        return _handled;
    }

    /////////////////////

    /**
     * 打包
     */
    public static XSocketMessage wrap(byte[] bytes) {
        return wrap("", "", bytes);
    }

    /**
     * 打包
     */
    public static XSocketMessage wrap(String resourceDescriptor, byte[] bytes) {
        return wrap(UUID.randomUUID().toString(), resourceDescriptor, bytes);
    }

    /**
     * 打包
     */
    public static XSocketMessage wrap(String key, String resourceDescriptor, byte[] bytes) {
        XSocketMessage msg = new XSocketMessage();

        msg.key = key;
        msg.resourceDescriptor = resourceDescriptor;
        msg.content = bytes;

        return msg;
    }
}
