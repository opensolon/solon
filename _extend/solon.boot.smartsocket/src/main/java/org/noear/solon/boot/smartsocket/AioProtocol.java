package org.noear.solon.boot.smartsocket;

import org.noear.solon.api.socket.XSocketMessage;

import org.noear.solon.api.socket.XSocketMessageUtils;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;


/**
 * /date/xxx/sss\n...
 *
 * */
public class AioProtocol implements Protocol<XSocketMessage> {

    @Override
    public XSocketMessage decode(ByteBuffer buffer, AioSession session) {
        return XSocketMessageUtils.decode(buffer);
    }
}

