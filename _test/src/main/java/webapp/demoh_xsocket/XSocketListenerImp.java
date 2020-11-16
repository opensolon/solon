package webapp.demoh_xsocket;

import org.noear.solon.SolonApp;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.annotation.ServerEndpoint;

@ServerEndpoint(value = "/demoe/websocket")
public class XSocketListenerImp implements Listener {
    @Override
    public void onMessage(Session session, Message message, boolean messageIsString) {
        System.out.println(session.path());

        if(Solon.cfg().isDebugMode()){
            return;
        }

        if (session.method() == MethodType.WEBSOCKET) {
            message.setHandled(true);

            session.getOpenSessions().forEach(s -> {
                s.send(message.toString());
            });
        } else {
            System.out.println("X我收到了::" + message.toString());
            //session.send("X我收到了::" + message.toString());
        }
    }
}
