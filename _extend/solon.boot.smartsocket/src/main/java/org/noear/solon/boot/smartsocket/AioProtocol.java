package org.noear.solon.boot.smartsocket;

import org.noear.solon.extend.socketapi.XSocketMessage;

import org.noear.solon.extend.socketapi.XSocketMessageUtils;
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
        if(buffer.remaining() < Integer.BYTES){
            return null;
        }

        buffer.mark();

        XSocketMessage tmp = XSocketMessageUtils.decode(buffer);

        if (tmp == null) {
            buffer.reset();
        }

        return tmp;
    }
}

