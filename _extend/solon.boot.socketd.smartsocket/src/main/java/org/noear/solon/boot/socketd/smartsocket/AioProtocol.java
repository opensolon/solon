package org.noear.solon.boot.socketd.smartsocket;

import org.noear.solon.boot.socketd.smartsocket.decoder.FixedLengthFrameDecoder;
import org.noear.solon.core.message.Message;

import org.noear.solon.extend.socketd.ProtocolManager;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;


/**
 * /date/xxx/sss\n...
 *
 * */
class AioProtocol implements Protocol<Message> {
    public static final AioProtocol instance = new AioProtocol();

    @Override
    public Message decode(ByteBuffer buffer, AioSession session) {
        FixedLengthFrameDecoder decoder = session.getAttachment();
        if(decoder == null){
            if (buffer.remaining() < Integer.BYTES) {
                return null;
            }else{
                buffer.mark();
                decoder = new FixedLengthFrameDecoder(buffer.getInt());
                buffer.reset();
                session.setAttachment(decoder);
            }
        }

        if(decoder.read(buffer) == false){
            return null;
        }else{
            session.setAttachment(null);
            buffer = decoder.getBuffer();
            buffer.flip();
        }

        return ProtocolManager.decode(buffer);
    }
}

