package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Message;
import org.noear.solon.core.util.HeaderUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

/**
 * SocketD 消息包，提供编码解码示例
 *
 * @author noear
 * @since 1.0
 * */
public class MessageUtils {

    //
    // encode & decode
    //

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


    //
    // warp
    //


    /**
     * 打包
     */
    public static Message wrap(byte[] body) {
        return wrap(null, null, body);
    }


    /**
     * 打包
     */
    public static Message wrap(String resourceDescriptor, String header, byte[] body) {
        return new Message(MessageFlag.message, UUID.randomUUID().toString(), resourceDescriptor, header, body);
    }


    /**
     * 打包
     */
    public static Message wrap(String key, String resourceDescriptor, String header, byte[] body) {
        return new Message(MessageFlag.message, key, resourceDescriptor, header, body);
    }


    //
    //属性打包
    //

    public static Message wrapJson(String resourceDescriptor, byte[] body) {
        return wrap(resourceDescriptor, "Content-Type=application/json", body);
    }

    public static Message wrapJson(String resourceDescriptor, String body) {
        try {
            return wrapJson(resourceDescriptor, body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 包装心跳包
     */
    public static Message wrapHeartbeat() {
        return new Message(MessageFlag.heartbeat, UUID.randomUUID().toString(), "", null, null);
    }


    /**
     * 包装握手包（只支持用头）
     */
    public static Message wrapHandshake(String header, byte[] body) {
        return new Message(MessageFlag.handshake, UUID.randomUUID().toString(), "", header, body);
    }

    public static Message wrapHandshake(String header) {
        return wrapHandshake(header, null);
    }

    public static Message wrapHandshake(Map<String, String> header) {
        return wrapHandshake(header, null);
    }

    public static Message wrapHandshake(Map<String, String> header, byte[] body) {
        return wrapHandshake(HeaderUtil.encodeHeaderMap(header), body);
    }


    /**
     * 包装响应包
     */
    public static Message wrapResponse(Message request, String header, byte[] body) {
        return new Message(MessageFlag.response, request.key(), request.resourceDescriptor(), header, body);
    }

    public static Message wrapResponse(Message request, byte[] body) {
        return wrapResponse(request, body);
    }
}