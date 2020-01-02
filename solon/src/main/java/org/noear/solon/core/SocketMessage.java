package org.noear.solon.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SocketMessage {
    public String resourceDescriptor;
    public byte[] content;
    public Charset charset = StandardCharsets.UTF_8;

    public SocketMessage(String resourceDescriptor, String str) {
        this.resourceDescriptor = resourceDescriptor;
        this.content = str.getBytes(charset);
    }

    public SocketMessage(String resourceDescriptor, byte[] bytes) {
        this.resourceDescriptor = resourceDescriptor;
        this.content = bytes;
    }

    public ByteBuffer wrap() throws IOException {
        byte[] rd = resourceDescriptor.getBytes(charset);
        int len = rd.length + content.length + 2;

        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(rd);
        buffer.putChar('\n');
        buffer.put(content);

        return buffer;
    }
}
