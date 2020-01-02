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

    private String readStr(ByteBuffer buffer, ByteBuffer sb) {
        sb.position(0);

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
            }
        }

        sb.flip();
        if (sb.limit() < 1) {
            return null;
        }

        return new String(sb.array(), 0, sb.limit());
    }
}

