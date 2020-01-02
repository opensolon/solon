package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;


/**
 * /date/xxx/sss\n...
 *
 * */
public class SsProtocol implements Protocol<SocketMessage> {

    private static final int URL_MAX_LEN = 256;

    @Override
    public SocketMessage decode(ByteBuffer buffer, AioSession<SocketMessage> session) {
        //: /\n,最少两个
        if (buffer.limit() < 2) {
            return null;
            //throw new RuntimeException("Protocol decoding error!");
        }

        //1.解码res
        ByteBuffer sb = ByteBuffer.allocate(Math.min(256,buffer.limit()));
        while (true) {
            byte c = buffer.get();

            if (c == '\n') {
                break;
            } else if (c > 32 || c < 0) {
                sb.put(c);
            }

            //url 太长了
            if (URL_MAX_LEN < buffer.position()) {
                return null;
                //throw new RuntimeException("Protocol decoding error!");
            }
        }

        sb.flip();
        if (sb.limit() < 1) {
            return null;
            //throw new RuntimeException("Protocol decoding error!");
        }

        String uri = new String(sb.array(), 0, sb.limit());

        //2.解码body
        byte[] bytes = new byte[buffer.limit() - buffer.position()];
        buffer.get(bytes, 0, buffer.limit() - buffer.position());

        return new SocketMessage(uri, bytes);
    }
}

