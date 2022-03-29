package org.noear.solon.socketd.client.smartsocket;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ListenerManager;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

class AioClientProcessor implements MessageProcessor<Message> {
    Session session;
    AioClientProcessor(Session session){
        this.session = session;
    }

    @Override
    public void process(AioSession s, Message message) {
        try {
            ListenerManager.getPipeline().onMessage(session, message);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stateEvent(AioSession s, StateMachineEnum state, Throwable throwable) {

        switch (state) {
            case NEW_SESSION:
                ListenerManager.getPipeline().onOpen(session);
                break;

            case SESSION_CLOSED:
                ListenerManager.getPipeline().onClose(session);
                AioSocketSession.remove(s);
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                ListenerManager.getPipeline().onError(session, throwable);
                break;
        }

    }
}