package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.extend.xsocket.XListenerProxy;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

public class AioProcessor implements MessageProcessor<Message> {

    @Override
    public void process(AioSession session, Message message) {
        try {
            MessageSession session1 = _SocketSession.get(session);

            XListenerProxy.getGlobal().onMessage(session1, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum state, Throwable throwable) {

        switch (state) {
            case NEW_SESSION:
                XListenerProxy.getGlobal().onOpen(_SocketSession.get(session));
                break;

            case SESSION_CLOSED:
                XListenerProxy.getGlobal().onClose(_SocketSession.get(session));
                _SocketSession.remove(session);
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                XListenerProxy.getGlobal().onError(_SocketSession.get(session), throwable);
                break;
        }

    }
}