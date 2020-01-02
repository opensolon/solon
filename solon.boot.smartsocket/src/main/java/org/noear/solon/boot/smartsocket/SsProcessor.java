package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

@SuppressWarnings("unchecked")
public class SsProcessor implements MessageProcessor<SocketMessage> {
    private SsContextHandler _contextHandler;
    public SsProcessor(SsContextHandler contextHandler){
        this._contextHandler = contextHandler;
    }


    @Override
    public void process(AioSession<SocketMessage> session, SocketMessage request) {
        try {
            SsContext context  =new SsContext(session, request);

            _contextHandler.handle(context);

            context.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateEvent(AioSession<SocketMessage> session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}