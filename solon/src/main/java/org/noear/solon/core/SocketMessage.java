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

    public static SocketMessage parse(ByteBuffer buffer){
        //: {key}\n/\n,最少两个
        if (buffer.limit() < 10) {
            return null;
        }

        //1.解码key and uri
        ByteBuffer sb = ByteBuffer.allocate(Math.min(256, buffer.limit()));

        String key = readStr(buffer, sb);
        if(key == null){
            return null;
        }

        String uri = readStr(buffer, sb);
        if(uri == null){
            return null;
        }

        //2.解码body
        byte[] bytes = new byte[buffer.limit() - buffer.position()];
        buffer.get(bytes, 0, buffer.limit() - buffer.position());

        return new SocketMessage(key, uri, bytes);
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


    private static String readStr(ByteBuffer buffer, ByteBuffer sb) {
        sb.position(0);

        while (true) {
            byte c = buffer.get();

            if (c == '\n') {
                break;
            } else if (c > 32 || c < 0) {
                sb.put(c);
            }

            //url 太长了
            if (256 < buffer.position()) {
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
