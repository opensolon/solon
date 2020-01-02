package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

public class AioProcessor implements MessageProcessor<SocketMessage> {
    private AioContextHandler _contextHandler;

    public AioProcessor(AioContextHandler contextHandler) {
        this._contextHandler = contextHandler;
    }


    @Override
    public void process(AioSession<SocketMessage> session, SocketMessage request) {
        try {
            _contextHandler.handle(session, request);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateEvent(AioSession<SocketMessage> session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}