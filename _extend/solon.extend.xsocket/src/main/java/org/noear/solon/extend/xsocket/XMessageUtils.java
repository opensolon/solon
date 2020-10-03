package org.noear.solon.extend.xsocket;

import java.nio.ByteBuffer;

/**
 * XSocket 消息包，提供编码解码示例
 * */
public class XMessageUtils {
    /**
     * 编码
     */
    public static ByteBuffer encode(XMessage msg) {
        byte[] keyB = msg.key().getBytes(msg.getCharset());
        byte[] rdB = msg.resourceDescriptor().getBytes(msg.getCharset());

        int len = keyB.length + rdB.length + msg.content().length + 2 * 2 + 4;

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
        buffer.put(msg.content());

        buffer.flip();

        return buffer;
    }

    /**
     * 解码
     */
    public static XMessage decode(ByteBuffer buffer) {
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

        return XMessage.wrap(key, uri, bytes);
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
