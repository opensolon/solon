package org.noear.solon.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

/**
 * XSocket 消息包（实现 XMessage + XListener 架构）
 *
 * @see XListener#onMessage(XSession, XMessage, boolean)
 * @author noear
 * @since 1.0
 * */
public class XMessage {
    /**
     * 1.消息key
     */
    private final String key;
    public String key() {
        return key;
    }

    /**
     * 2.资源描述
     */
    private final String resourceDescriptor;
    public String resourceDescriptor() {
        return resourceDescriptor;
    }

    /**
     * 3.消息内容
     */
    private final byte[] content;
    public byte[] content() {
        return content;
    }

    //////////////////////////////////////////

    /**
     * 消息转换
     * */
    public <T> T map(Function<XMessage,T> mapper){
        return mapper.apply(this);
    }

    //////////////////////////////////////////

    private XMessage(String key, String resourceDescriptor, byte[] bytes) {
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

    private boolean _request;
    public void setRequest(boolean request) {
        _request = request;
    }
    public boolean isRequest() {
        return _request;
    }
    //////////////////////////////////////////

    /**
     * 打包
     */
    public static XMessage wrap(byte[] bytes) {
        return wrap("", "", bytes);
    }

    /**
     * 打包
     */
    public static XMessage wrap(String resourceDescriptor, byte[] bytes) {
        return wrap(UUID.randomUUID().toString(), resourceDescriptor, bytes);
    }

    /**
     * 打包
     */
    public static XMessage wrap(String key, String resourceDescriptor, byte[] bytes) {
        return new XMessage(key, resourceDescriptor, bytes);
    }

}
