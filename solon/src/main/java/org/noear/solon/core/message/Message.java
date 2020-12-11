package org.noear.solon.core.message;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.util.HeaderUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * SocketD 消息包（实现 Message + Listener 架构）
 *
 * 格式一：{flag + body}
 * 格式二：{flag + key + resourceDescriptor + header + body}
 *
 * @see Listener#onMessage(Session, Message, boolean)
 * @author noear
 * @since 1.0
 * */
public class Message{
    /**
     * 1.消息标志
     */
    private final int flag;
    @Note("1.消息标志")
    public int flag() {
        return flag;
    }

    /**
     * 2.消息key
     */
    private final String key;
    @Note("2.消息key")
    public String key() {
        return key;
    }

    /**
     * 3.资源描述
     */
    private final String resourceDescriptor;
    @Note("3.资源描述")
    public String resourceDescriptor() {
        return resourceDescriptor;
    }


    private final String header;

    @Note("4.消息头")
    public String header() {
        return header;
    }

    private Map<String,String> headerMap;
    public Map<String,String> headerMap() {
        if (headerMap == null) {
            headerMap = HeaderUtil.decodeHeaderMap(header);
        }

        return Collections.unmodifiableMap(headerMap);
    }


    /**
     * 5.消息主体
     */
    private final byte[] body;
    @Note("5.消息主体")
    public byte[] body() {
        return body;
    }

    public String bodyAsString() {
        if (body == null) {
            return null;
        } else {
            return new String(body, charset);
        }
    }

    //////////////////////////////////////////

    /**
     * 消息转换
     */
    public <T> T map(Function<Message, T> mapper) {
        return mapper.apply(this);
    }

    //////////////////////////////////////////

    public Message(int flag, String key, String resourceDescriptor, String header, byte[] body) {
        this.flag = flag;
        this.key = (key == null ? "" : key);
        this.resourceDescriptor = (resourceDescriptor == null ? "" : resourceDescriptor);
        this.header = (header == null ? "" : header);
        this.body = (body == null ? new byte[]{} : body);
    }


    @Override
    public String toString() {
        return "Message{" +
                "flag=" + flag() +
                ", key='" + key() + '\'' +
                ", resourceDescriptor='" + resourceDescriptor() + '\'' +
                ", header='" + header() + '\'' +
                ", body='" + bodyAsString() + '\'' +
                '}';
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


}
