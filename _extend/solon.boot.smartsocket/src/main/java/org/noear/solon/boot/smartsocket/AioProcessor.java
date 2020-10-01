package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.XMethod;
import org.noear.solonx.socket.api.XSession;
import org.noear.solonx.socket.api.XSocketHandler;
import org.noear.solonx.socket.api.XSocketListener;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;
import org.noear.solonx.socket.api.XSocketMessage;

import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

public class AioProcessor implements MessageProcessor<XSocketMessage> {
    private XSocketHandler handler;
    private XSocketListener listening;
    public AioProcessor() {
        handler = new XSocketHandler(XMethod.SOCKET);
        Aop.getAsyn(XSocketListener.class, (bw) -> listening = bw.raw());
    }

    @Override
    public void process(AioSession session, XSocketMessage message) {
        try {
            XSession session1 = _SocketSession.get(session);
            if (listening != null) {
                listening.onMessage(session1, message);
            }

            handler.handle(session1, message, false);
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum state, Throwable throwable) {
        if(listening != null) {
            switch (state) {
                case NEW_SESSION:
                    listening.onOpen(_SocketSession.get(session));
                    break;

                case SESSION_CLOSING:
                    listening.onClosing(_SocketSession.get(session));
                    _SocketSession.remove(session);
                    break;

                case SESSION_CLOSED:
                    listening.onClose(_SocketSession.get(session));
                    _SocketSession.remove(session);
                    break;

                case PROCESS_EXCEPTION:
                case DECODE_EXCEPTION:
                case INPUT_EXCEPTION:
                case ACCEPT_EXCEPTION:
                case OUTPUT_EXCEPTION:
                    listening.onError(_SocketSession.get(session), throwable);
                    break;
            }
        }
    }
}