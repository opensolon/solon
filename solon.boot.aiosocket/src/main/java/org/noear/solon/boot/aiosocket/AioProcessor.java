package org.noear.solon.boot.aiosocket;

import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class AioProcessor implements CompletionHandler<Integer, ByteBuffer> {
    AioSession session;

    public AioProcessor(AioSession session){
        this.session = session;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();

        try {
            SocketMessage msg = SocketMessage.decode(attachment);
            AioContext context = new AioContext(session, msg);
            XApp.global().handle(context);

        }catch (Throwable ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
