package org.noear.solonx.socket.api;

import java.nio.ByteBuffer;
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

    /////////////////////

    private XSocketMessage(String key, String resourceDescriptor, byte[] bytes) {
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
        return new XSocketMessage(key, resourceDescriptor, bytes);
    }

    /////////////////////
    /**
     * 编码
     */
    public ByteBuffer encode() {
        byte[] keyB = this.key().getBytes(this.getCharset());
        byte[] rdB = this.resourceDescriptor().getBytes(this.getCharset());

        int len = keyB.length + rdB.length + this.content().length + 2 * 2 + 4;

        ByteBuffer buffer = ByteBuffer.allocate(len);

        //长度
        buffer.putInt(len);

        //key
        buffer.put(keyB);
        buffer.putChar('\n');

        //resourceDescriptor
        buffer.put(rdB);
        buffer.putChar('\n');

        //content
        buffer.put(this.content());

        buffer.flip();

        return buffer;
    }

    /**
     * 解码
     */
    public static XSocketMessage decode(ByteBuffer buffer) {
        int len0 = buffer.getInt();

        if(len0 < buffer.remaining()){
            return null;
        }

        //1.解码key and uri
        ByteBuffer sb = ByteBuffer.allocate(Math.min(256, buffer.limit()));

        String key = decodeString(buffer, sb);
        if (key == null) {
            return null;
        }

        String uri = decodeString(buffer, sb);
        if (uri == null) {
            return null;
        }

        //2.解码body
        int len = len0 - buffer.position();
        byte[] bytes = new byte[len];
        if (len > 0) {
            buffer.get(bytes, 0, len);
        }

        return XSocketMessage.wrap(key, uri, bytes);
    }


    private static String decodeString(ByteBuffer buffer, ByteBuffer sb) {
        sb.clear();

        while (true) {
            byte c = buffer.get();

            if (c == 10) { //10:'\n'
                break;
            } else if (c != 0) { //32:' '
                sb.put(c);
            }

            //url 太长了
            if (256 < sb.position()) {
                return null;
            }
        }

        sb.flip();
        if (sb.limit() < 1) {
            return null;
        }

        return new String(sb.array(), 0, sb.limit());
    }
}
