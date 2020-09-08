package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XEventBus;
import org.noear.solonclient.channel.SocketMessage;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

public class AioProcessor implements MessageProcessor<SocketMessage> {
    @Override
    public void process(AioSession session, SocketMessage request) {
        try {
            handle(session, request);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }

    public void handle(AioSession session, SocketMessage request) {
        if (request == null) {
            return;
        }

        try {
            AioContext context = new AioContext(session, request);

            XApp.global().tryHandle(context);

            context.commit();

        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }
}