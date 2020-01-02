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
        return SocketMessage.parse(buffer);
    }
}

