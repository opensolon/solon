package webapp.demoh_xsocket;

import org.noear.solon.Solon;
import org.noear.solon.core.handler.MethodType;
import org.noear.solon.core.message.MessageListener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.annotation.ServerEndpoint;

@ServerEndpoint(value = "/demoe/websocket")
public class XSocketListenerImp implements MessageListener {
    @Override
    public void onMessage(MessageSession session, Message message, boolean messageIsString) {
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
