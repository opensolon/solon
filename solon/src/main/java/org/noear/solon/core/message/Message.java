package org.noear.solon.core.message;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

/**
 * XSocket 消息包（实现 Message + Listener 架构）
 *
 * @see Listener#onMessage(Session, Message, boolean)
 * @author noear
 * @since 1.0
 * */
public class Message {
    /**
     * 1.消息标志（0发起； 1响应）
     */
    private final int flag;

    public int flag() {
        return flag;
    }

    /**
     * 2.消息key
     */
    private final String key;

    public String key() {
        return key;
    }

    /**
     * 3.资源描述
     */
    private final String resourceDescriptor;

    public String resourceDescriptor() {
        return resourceDescriptor;
    }

    /**
     * 4.消息内容
     */
    private final byte[] content;

    public byte[] content() {
        return content;
    }

    //////////////////////////////////////////

    /**
     * 消息转换
     */
    public <T> T map(Function<Message, T> mapper) {
        return mapper.apply(this);
    }

    //////////////////////////////////////////

    private Message(int flag, String key, String resourceDescriptor, byte[] bytes) {
        this.flag = flag;

        this.key = (key == null ? "" : key);
        this.resourceDescriptor = (resourceDescriptor == null ? "" : resourceDescriptor);
        this.content = bytes;
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
        if (charset != null) {
            this.charset = charset;
        }
    }

    //////////////////////////////////////////

    private boolean _handled;
    public void setHandled(boolean handled) {
        _handled = handled;
    }
    public boolean getHandled() {
        return _handled;
    }

    //////////////////////////////////////////

    /**
     * 打包
     */
    public static Message wrap(byte[] bytes) {
        return wrap(0, "", "", bytes);
    }

    /**
     * 打包
     */
    public static Message wrap(String resourceDescriptor, byte[] bytes) {
        return wrap(0, UUID.randomUUID().toString(), resourceDescriptor, bytes);
    }

    public static Message wrap(String key, String resourceDescriptor, byte[] bytes) {
        return wrap(0, key, resourceDescriptor, bytes);
    }

    /**
     * 打包
     */
    public static Message wrap(int flag, String key, String resourceDescriptor, byte[] bytes) {
        return new Message(flag, key, resourceDescriptor, bytes);
    }
}
