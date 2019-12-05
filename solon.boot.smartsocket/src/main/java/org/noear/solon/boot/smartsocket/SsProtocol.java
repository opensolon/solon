package org.noear.solon.boot.smartsocket;

import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;


public class SsProtocol implements Protocol<byte[]> {

    private static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;

    @Override
    public byte[] decode(ByteBuffer data, AioSession<byte[]> session) {
        int remaining = data.remaining();
        if (remaining < INTEGER_BYTES) {
            return null;
        }
        int messageSize = data.getInt(data.position());
        if (messageSize > remaining) {
            return null;
        }
        byte[] data2 = new byte[messageSize - INTEGER_BYTES];
        data.getInt();
        data.get(data2);

        return data2;
    }
}

