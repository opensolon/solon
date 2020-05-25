package org.noear.solon.boot.fineio;

import org.noear.fineio.Protocol;
import org.noear.solon.core.SocketMessage;

import java.nio.ByteBuffer;

public class SocketMessageProtocol implements Protocol<SocketMessage> {
    @Override
    public SocketMessage decode(ByteBuffer buffer) {
        return SocketMessage.decode(buffer);
    }
}
