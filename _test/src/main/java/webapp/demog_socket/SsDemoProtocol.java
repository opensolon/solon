package webapp.demog_socket;

import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;


/**
 * /date/xxx/sss\n...
 *
 * */
public class SsDemoProtocol implements Protocol<SocketMessage> {

    private static final int URL_MAX_LEN = 256;

    @Override
    public SocketMessage decode(ByteBuffer buffer, AioSession<SocketMessage> session) {
        //: /\n,最少两个
        if (buffer.limit() < 2) {
            return null;
            //throw new RuntimeException("Protocol decoding error!");
        }

        //1.解码res
        StringBuilder sb = new StringBuilder(256);
        while (true) {
            char c = (char) (buffer.get() & 0xFF);

            if (c == '\n') {
                break;
            } else if (c != ' ') {
                sb.append(c);
            }

            //url 太长了
            if (URL_MAX_LEN < buffer.position()) {
                return null;
                //throw new RuntimeException("Protocol decoding error!");
            }
        }

        if (sb.length() < 1) {
            return null;
            //throw new RuntimeException("Protocol decoding error!");
        }

        //2.解码body
        byte[] bytes = new byte[buffer.limit() - buffer.position()];
        buffer.get(bytes, 0, buffer.limit() - buffer.position());
        return new SocketMessage(sb.toString(), bytes);
    }
}

