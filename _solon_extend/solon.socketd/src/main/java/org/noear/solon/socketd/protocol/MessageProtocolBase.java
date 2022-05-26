package org.noear.solon.socketd.protocol;

import org.noear.solon.core.message.MessageFlag;
import org.noear.solon.core.message.Message;

import java.nio.ByteBuffer;

/**
 * 基础协议
 *
 * @author noear
 * @since 1.2
 * */
public class MessageProtocolBase implements MessageProtocol {
    public static final MessageProtocol instance = new MessageProtocolBase();

    @Override
    public ByteBuffer encode(Message message) throws Exception {
        if (message.flag() == MessageFlag.container) {
            //length (flag + content + int.bytes)
            int len = message.body().length + 4 + 4;

            ByteBuffer buffer = ByteBuffer.allocate(len);

            //长度
            buffer.putInt(len);

            //flag
            buffer.putInt(message.flag());

            //content
            buffer.put(message.body());

            buffer.flip();

            return buffer;
        } else {
            //key
            byte[] keyB = message.key().getBytes(message.getCharset());
            //resourceDescriptor
            byte[] resourceDescriptorB = message.resourceDescriptor().getBytes(message.getCharset());
            //header
            byte[] headerB = message.header().getBytes(message.getCharset());

            //length (flag + key + resourceDescriptor + content + int.bytes)
            int len = keyB.length + resourceDescriptorB.length + headerB.length + message.body().length + 2 * 3 + 4 + 4;

            ByteBuffer buffer = ByteBuffer.allocate(len);

            //长度
            buffer.putInt(len);

            //flag
            buffer.putInt(message.flag());

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
            buffer.put(message.body());

            buffer.flip();

            return buffer;
        }
    }

    @Override
    public Message decode(ByteBuffer buffer) throws Exception {
        int len0 = buffer.getInt();

        if (len0 > (buffer.remaining() + 4)) {
            return null;
        }

        int flag = buffer.getInt();

        if (flag == MessageFlag.container) {
            //2.解码 content
            int len = len0 - buffer.position();
            byte[] body = new byte[len];
            if (len > 0) {
                buffer.get(body, 0, len);
            }

            return new Message(flag, null, null, null, body);
        } else {
            //1.解码key and resourceDescriptor
            ByteBuffer sb = ByteBuffer.allocate(Math.min(4096, buffer.limit()));

            //key
            String key = decodeString(buffer, sb, 256);
            if (key == null) {
                return null;
            }

            //resourceDescriptor
            String resourceDescriptor = decodeString(buffer, sb, 512);
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
    }

    protected String decodeString(ByteBuffer buffer, ByteBuffer sb, int maxLen) {
        sb.clear();

        while (true) {
            byte c = buffer.get();

            if (c == 10) { //10:'\n'
                break;
            } else if (c != 0) { //32:' '
                sb.put(c);
            }

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
}
