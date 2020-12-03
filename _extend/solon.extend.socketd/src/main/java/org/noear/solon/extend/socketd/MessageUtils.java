package org.noear.solon.extend.socketd;

import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.message.Message;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * SocketD 消息包，提供编码解码示例
 *
 * @author noear
 * @since 1.0
 * */
public class MessageUtils {
    /**
     * 编码
     */
    public static ByteBuffer encode(Message msg) {
        //key
        byte[] keyB = msg.key().getBytes(msg.getCharset());
        //resourceDescriptor
        byte[] resourceDescriptorB = msg.resourceDescriptor().getBytes(msg.getCharset());
        //header
        byte[] headerB = msg.header().getBytes(msg.getCharset());

        //length (flag + key + resourceDescriptor + content)
        int len = keyB.length + resourceDescriptorB.length + headerB.length + msg.body().length + 2 * 3 + 4 + 4;

        ByteBuffer buffer = ByteBuffer.allocate(len);

        //长度
        buffer.putInt(len);

        //flag
        buffer.putInt(msg.flag());

        //key
        buffer.put(keyB);
        buffer.putChar('\n');

        //resourceDescriptor
        buffer.put(resourceDescriptorB);
        buffer.putChar('\n');
        //header
        buffer.put(headerB);
        buffer.putChar('\n');

        //content
        buffer.put(msg.body());

        buffer.flip();

        return buffer;
    }

    /**
     * 解码
     */
    public static Message decode(ByteBuffer buffer) {
        int len0 = buffer.getInt();

        if (len0 > (buffer.remaining() + 4)) {
            return null;
        }

        int flag = buffer.getInt();

        //1.解码key and resourceDescriptor
        ByteBuffer sb = ByteBuffer.allocate(Math.min(1024, buffer.limit()));

        //key
        String key = decodeString(buffer, sb, 256);
        if (key == null) {
            return null;
        }

        //resourceDescriptor
        String resourceDescriptor = decodeString(buffer, sb, 256);
        if (resourceDescriptor == null) {
            return null;
        }

        //header
        String header = decodeString(buffer, sb, 0);
        if (header == null) {
            return null;
        }

        //2.解码 content
        int len = len0 - buffer.position();
        byte[] body = new byte[len];
        if (len > 0) {
            buffer.get(body, 0, len);
        }

        return new Message(flag, key, resourceDescriptor, header, body);
    }


    private static String decodeString(ByteBuffer buffer, ByteBuffer sb, int maxLen) {
        sb.clear();

        while (true) {
            byte c = buffer.get();

            if (c == 10) { //10:'\n'
                break;
            } else if (c != 0) { //32:' '
                sb.put(c);
            }

            //url 太长了
            if (maxLen > 0 && maxLen < sb.position()) {
                return null;
            }
        }

        sb.flip();
        if (sb.limit() < 1) {
            return "";
        }

        return new String(sb.array(), 0, sb.limit());
    }

    public static String encodeHeaderMap(Map<String, String> headers) {
        StringBuilder header = new StringBuilder();
        if (headers != null) {
            headers.forEach((k, v) -> {
                header.append(k).append("=").append(v).append("&");
            });
        }

        return header.toString();
    }

    public static Map<String, String> decodeHeaderMap(String header) {
        NvMap headerMap = new NvMap();

        if (Utils.isNotEmpty(header)) {
            String[] ss = header.split("&");
            for (String s : ss) {
                String[] kv = s.split("=");

                headerMap.put(kv[0], kv[1]);
            }
        }

        return headerMap;
    }
}