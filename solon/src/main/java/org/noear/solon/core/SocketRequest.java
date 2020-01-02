package org.noear.solon.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SocketRequest {
    public String resourceDescriptor;
    public byte[] message;
    public Charset charset = StandardCharsets.UTF_8;

    public SocketRequest(String resourceDescriptor, String message) {
        this.resourceDescriptor = resourceDescriptor;
        this.message = message.getBytes(charset);
    }

    public SocketRequest(String resourceDescriptor, byte[] bytes) {
        this.resourceDescriptor = resourceDescriptor;
        this.message = bytes;
    }

    public ByteBuffer wrap() throws IOException {
        int len = resourceDescriptor.length() + message.length;

        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(resourceDescriptor.getBytes(charset));
        buffer.putChar('\n');
        buffer.put(message);

        return buffer;
    }
}
