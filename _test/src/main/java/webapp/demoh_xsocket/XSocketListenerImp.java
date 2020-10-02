package webapp.demoh_xsocket;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XBean;
import org.noear.solon.core.XSignal;
import org.noear.solonx.socket.api.XSession;
import org.noear.solonx.socket.api.XSocketListener;
import org.noear.solonx.socket.api.XSocketMessage;

@XBean
public class XSocketListenerImp implements XSocketListener {
    @Override
    public void onMessage(XSession session, XSocketMessage message) {
        System.out.println(message.resourceDescriptor());

        if(XApp.cfg().isDebugMode()){
            return;
        }

        if (session.signal() == XSignal.WEBSOCKET) {
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
