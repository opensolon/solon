package org.noear.solon.boot.smartsocket;

import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.io.ByteArrayInputStream;

@SuppressWarnings("unchecked")
public class SsProcessor implements MessageProcessor<byte[]> {
    private SsContextHandler _contextHandler;
    public SsProcessor(SsContextHandler contextHandler){
        this._contextHandler = contextHandler;
    }

    @Override
    public void process(AioSession<byte[]> session, byte[] msg) {

        try {
            SsContext context  =new SsContext(session, new ByteArrayInputStream(msg));

            _contextHandler.handle(context);

            context.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateEvent(AioSession<byte[]> session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}