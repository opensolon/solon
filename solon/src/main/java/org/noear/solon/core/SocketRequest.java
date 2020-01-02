package org.noear.solon.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SocketRequest {
    public String resourceDescriptor;
    public InputStream message;
    public Charset charset = StandardCharsets.UTF_8;

    public SocketRequest(String resourceDescriptor, String message) {
        this.resourceDescriptor = resourceDescriptor;
        this.message = new ByteArrayInputStream(message.getBytes(charset));
    }

    public SocketRequest(String resourceDescriptor, byte[] bytes) {
        this.resourceDescriptor = resourceDescriptor;
        this.message = new ByteArrayInputStream(bytes);
    }

    public ByteBuffer wrap() throws IOException {
        int len = resourceDescriptor.length();
        try{ len = len + message.available(); }catch (IOException ex){}

        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(resourceDescriptor.getBytes(charset));
        buffer.putChar('\n');


        byte[] b = new byte[1024];
        int lens = -1;
        while ((lens = message.read(b)) > 0) {
            buffer.put(b, 0, lens);
        }

        return buffer;
    }
}
