package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.XMethod;
import org.noear.solon.extend.socketapi.*;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;

import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

public class AioProcessor implements MessageProcessor<XSocketMessage> {
    private XSocketContextHandler handler;
    private XSocketListener listener;
    public AioProcessor() {
        handler = new XSocketContextHandler(XMethod.SOCKET);
        listener = XSocketListenerProxy.getInstance();
    }

    @Override
    public void process(AioSession session, XSocketMessage message) {
        try {
            XSession session1 = _SocketSession.get(session);
            if (listener != null) {
                listener.onMessage(session1, message);
            }

            if (message.getHandled() == false) {
                handler.handle(session1, message, false);
            }
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum state, Throwable throwable) {
        if(listener != null) {
            switch (state) {
                case NEW_SESSION:
                    listener.onOpen(_SocketSession.get(session));
                    break;

                case SESSION_CLOSING:
                    listener.onClosing(_SocketSession.get(session));
                    _SocketSession.remove(session);
                    break;

                case SESSION_CLOSED:
                    listener.onClose(_SocketSession.get(session));
                    _SocketSession.remove(session);
                    break;

                case PROCESS_EXCEPTION:
                case DECODE_EXCEPTION:
                case INPUT_EXCEPTION:
                case ACCEPT_EXCEPTION:
                case OUTPUT_EXCEPTION:
                    listener.onError(_SocketSession.get(session), throwable);
                    break;
            }
        }
    }
}