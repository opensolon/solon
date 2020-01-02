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

    @Override
    public SocketMessage decode(ByteBuffer buffer, AioSession<SocketMessage> session) {
        return SocketMessage.decode(buffer);
    }
}

