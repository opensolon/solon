package org.noear.solon.socketd;

import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;

import java.util.Collection;

/**
 * 会话管理者。为服务端提供统一的管理接口
 *
 * @author noear
 * @since 1.2
 * */
public abstract class SessionManager {
    private static SessionManager socket;
    private static SessionManager websocket;

    public static void register(SessionManager factory) {
        if (factory.signalType() == SignalType.SOCKET) {
            socket = factory;
        }

        if (factory.signalType() == SignalType.WEBSOCKET) {
            websocket = factory;
        }
    }

    public static SessionManager socket(){
        if(socket == null){
            throw new IllegalArgumentException("Socket session manager uninitialized");
        }

        return socket;
    }

    public static SessionManager websocket(){
        if(websocket == null){
            throw new IllegalArgumentException("WeSocket session manager uninitialized");
        }

        return websocket;
    }


    protected abstract SignalType signalType();

    public abstract Session getSession(Object conn);

    public abstract Collection<Session> getOpenSessions();

    public abstract void removeSession(Object conn);

}
