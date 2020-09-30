package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.api.socket.SocketListening;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;
import org.noear.solon.api.socket.SocketMessage;

import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

public class AioProcessor implements MessageProcessor<SocketMessage> {
    private SocketListening listening;
    public AioProcessor() {
        Aop.getAsyn(SocketListening.class, (bw) -> listening = bw.raw());
    }

    @Override
    public void process(AioSession session, SocketMessage request) {
        try {
            if (listening != null) {
                listening.onMessage(_SocketSession.get(session), request);
            }else {
                handle(session, request);
            }
        } catch (Throwable e) {
            e.printStackTrace();
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

    public void handle(AioSession session, SocketMessage request) {
        if (request == null) {
            return;
        }

        try {
            AioContext context = new AioContext(session, request);

            XApp.global().tryHandle(context);

            if(context.getHandled()) {
                context.commit();
            }

        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }
}