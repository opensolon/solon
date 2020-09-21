package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.SocketMessage;

import java.nio.ByteBuffer;
import java.util.UUID;

public class SocketMessageUtils {
    /**
     * 打包
     * */
    public static SocketMessage wrap(String resourceDescriptor, byte[] bytes) {
        return wrap(UUID.randomUUID().toString(), resourceDescriptor, bytes);
    }

    /**
     * 打包
     * */
    public static SocketMessage wrap(String key ,String resourceDescriptor, byte[] bytes) {
        SocketMessage msg = new SocketMessage();

        msg.key = key;
        msg.resourceDescriptor = resourceDescriptor;
        msg.content = bytes;

        return msg;
    }

    /**
     * 解码
     * */
    public static SocketMessage decode(ByteBuffer buffer) {
        //1.解码key and uri
        ByteBuffer sb = ByteBuffer.allocate(Math.min(256, buffer.limit()));

        int len0 = buffer.getInt();

        String key = readStr(buffer, sb);
        if (key == null) {
            return null;
        }

        String uri = readStr(buffer, sb);
        if (uri == null) {
            return null;
        }

        //2.解码body
        int len = len0 - buffer.position();
        byte[] bytes = new byte[len];
        buffer.get(bytes, 0, len);

        SocketMessage msg = new SocketMessage();
        msg.key = key;
        msg.resourceDescriptor = uri;
        msg.content = bytes;

        return msg;
    }

    /**
     * 编码
     */
    public static ByteBuffer encode(SocketMessage msg) {
        byte[] keyB = msg.key.getBytes(msg.charset);
        byte[] rdB = msg.resourceDescriptor.getBytes(msg.charset);

        int len = keyB.length + rdB.length + msg.content.length + 2 * 2 + 4;

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
        buffer.put(msg.content);

        buffer.flip();

        return buffer;
    }

    public static String getString(SocketMessage msg) {
        if (msg.content == null) {
            return null;
        } else {
            return new String(msg.content, msg.charset);
        }
    }

    private static String readStr(ByteBuffer buffer, ByteBuffer sb) {
        sb.clear();

        while (true) {
            byte c = buffer.get();

            if (c == 10) { //10:'\n'
                break;
            } else if( c != 0){ //32:' '
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
