package webapp.demoh_xsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XSignal;
import org.noear.solon.socket.XListener;
import org.noear.solon.socket.XMessage;
import org.noear.solon.socket.XSession;
import org.noear.solon.socket.XSignalEndpoint;

@XSignalEndpoint("/demoe/websocket")
public class XSocketListenerImp implements XListener {
    @Override
    public void onMessage(XSession session, XMessage message) {
        System.out.println(session.resourceDescriptor());

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
