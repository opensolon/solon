package org.noear.solon.boot.socketd.smartsocket;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.client.smartsocket.AioSocketSession;


import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

class AioServerProcessor implements MessageProcessor<Message> {
    @Override
    public void process(AioSession session, Message message) {
        try {
            Session session1 = AioSocketSession.get(session);

            Solon.app().listener().onMessage(session1, message);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum state, Throwable throwable) {

        switch (state) {
            case NEW_SESSION:
                Solon.app().listener().onOpen(AioSocketSession.get(session));
                break;

            case SESSION_CLOSED:
                Solon.app().listener().onClose(AioSocketSession.get(session));
                AioSocketSession.remove(session);
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                Solon.app().listener().onError(AioSocketSession.get(session), throwable);
                break;
        }

    }
}