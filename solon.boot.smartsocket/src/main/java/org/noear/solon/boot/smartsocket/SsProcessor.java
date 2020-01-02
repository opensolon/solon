package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.SocketRequest;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

@SuppressWarnings("unchecked")
public class SsProcessor implements MessageProcessor<SocketRequest> {
    private SsContextHandler _contextHandler;
    public SsProcessor(SsContextHandler contextHandler){
        this._contextHandler = contextHandler;
    }


    @Override
    public void process(AioSession<SocketRequest> session, SocketRequest ssRequest) {
        try {
            SsContext context  =new SsContext(session, ssRequest);

            _contextHandler.handle(context);

            context.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateEvent(AioSession<SocketRequest> session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}