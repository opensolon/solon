package webapp.demoh_xsocket;

import org.noear.solon.annotation.XBean;
import org.noear.solonx.socket.api.XSession;
import org.noear.solonx.socket.api.XSocketListener;
import org.noear.solonx.socket.api.XSocketMessage;

@XBean
public class XSocketListenerImp implements XSocketListener {
    @Override
    public void onMessage(XSession session, XSocketMessage message) {
        //System.out.println("X我收到了::" + message.toString());
        //session.send("X我收到了::" + message.toString());

        System.out.println(message.resourceDescriptor());

        message.setHandled(true);

        session.getOpenSessions().forEach(s -> {
            s.send(message.toString());
        });
    }
}
