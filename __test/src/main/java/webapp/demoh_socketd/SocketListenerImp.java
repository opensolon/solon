package webapp.demoh_socketd;

import org.noear.solon.Solon;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;

import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(path = "**")
public class SocketListenerImp extends SimpleWebSocketListener {
    private Set<WebSocket> sessionMap = new HashSet<>();
    @Override
    public void onMessage(WebSocket session, String message) {
        System.out.println(session.getPath());

        if (Solon.cfg().isDebugMode()) {
            return;
        }


            sessionMap.forEach(s -> {
                s.send(message + "-" + sessionMap.size() + "-" + session.getHandshake().getParamMap());
            });

    }

    @Override
    public void onOpen(WebSocket session)  {
        sessionMap.add(session);
    }

    @Override
    public void onClose(WebSocket session) {
        sessionMap.remove(session);
    }
}
