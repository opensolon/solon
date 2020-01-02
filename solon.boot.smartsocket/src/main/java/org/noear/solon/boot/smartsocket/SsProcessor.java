package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unchecked")
public class SsProcessor implements MessageProcessor<SocketMessage> {
    private SsContextHandler _contextHandler;
    private ExecutorService _pool = Executors.newCachedThreadPool();

    public SsProcessor(SsContextHandler contextHandler) {
        this._contextHandler = contextHandler;
    }


    @Override
    public void process(AioSession<SocketMessage> session, SocketMessage request) {
        if (request == null) {
            return;
        }

        _pool.execute(() -> {
            try {
                _contextHandler.handle(session, request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stateEvent(AioSession<SocketMessage> session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}