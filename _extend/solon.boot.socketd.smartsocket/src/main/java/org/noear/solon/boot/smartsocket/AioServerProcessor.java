package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ListenerProxy;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

class AioServerProcessor implements MessageProcessor<Message> {
    @Override
    public void process(AioSession session, Message message) {
        try {
            Session session1 = _SocketSession.get(session);

            ListenerProxy.getGlobal().onMessage(session1, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum state, Throwable throwable) {

        switch (state) {
            case NEW_SESSION:
                ListenerProxy.getGlobal().onOpen(_SocketSession.get(session));
                break;

            case SESSION_CLOSED:
                ListenerProxy.getGlobal().onClose(_SocketSession.get(session));
                _SocketSession.remove(session);
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                ListenerProxy.getGlobal().onError(_SocketSession.get(session), throwable);
                break;
        }

    }
}