package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ListenerProxy;
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
            ListenerProxy.getGlobal().onMessage(session, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stateEvent(AioSession s, StateMachineEnum state, Throwable throwable) {

        switch (state) {
            case NEW_SESSION:
                ListenerProxy.getGlobal().onOpen(session);
                break;

            case SESSION_CLOSED:
                ListenerProxy.getGlobal().onClose(session);
                _SocketSession.remove(s);
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                ListenerProxy.getGlobal().onError(session, throwable);
                break;
        }

    }
}