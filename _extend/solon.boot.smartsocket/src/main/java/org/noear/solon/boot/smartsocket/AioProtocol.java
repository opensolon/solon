package org.noear.solon.boot.smartsocket;

import org.noear.solonclient.channel.SocketMessage;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;


/**
 * /date/xxx/sss\n...
 *
 * */
public class AioProtocol implements Protocol<SocketMessage> {

    @Override
    public SocketMessage decode(ByteBuffer buffer, AioSession session) {
        return SocketMessage.decode(buffer);
    }
}

