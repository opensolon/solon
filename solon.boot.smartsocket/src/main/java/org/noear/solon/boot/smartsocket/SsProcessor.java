package org.noear.solon.boot.smartsocket;

import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

public class SsProcessor implements MessageProcessor<String> {

    SsContextHandler _handler;

    public SsProcessor(SsContextHandler handler){
        _handler = handler;
    }

    @Override
    public void process(AioSession<String> session, String msg) {
        SsContext context = new SsContext(session, msg);
        _handler.handle(context);
    }

    @Override
    public void stateEvent(AioSession<String> session, StateMachineEnum stateMachineEnum, Throwable throwable) {
    }
}