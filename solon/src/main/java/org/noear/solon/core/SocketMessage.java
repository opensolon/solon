package org.noear.solon.core;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SocketMessage {
    public String key;
    public String resourceDescriptor;
    public byte[] content;
    public Charset charset = StandardCharsets.UTF_8;

    public SocketMessage(String key, String resourceDescriptor, String str) {
        this.key = key;
        this.resourceDescriptor = resourceDescriptor;
        this.content = str.getBytes(charset);
    }

    public SocketMessage(String key, String resourceDescriptor, byte[] bytes) {
        this.key = key;
        this.resourceDescriptor = resourceDescriptor;
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
     * 打包
     * */
    public ByteBuffer wrap() {
        byte[] keyB = key.getBytes(charset);
        byte[] rdB = resourceDescriptor.getBytes(charset);

        int len = keyB.length + rdB.length + content.length + 2 * 2;

        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(keyB);
        buffer.putChar('\n');

        buffer.put(rdB);
        buffer.putChar('\n');

        buffer.put(content);

        return buffer;
    }
}
