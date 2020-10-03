package webapp.demoh_xsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XMethod;
import org.noear.solon.xsocket.XListener;
import org.noear.solon.xsocket.XMessage;
import org.noear.solon.xsocket.XSession;
import org.noear.solon.xsocket.XServerEndpoint;

@XServerEndpoint(value = "/demoe/websocket")
public class XSocketListenerImp implements XListener {
    @Override
    public void onMessage(XSession session, XMessage message) {
        System.out.println(session.resourceDescriptor());

        if(XApp.cfg().isDebugMode()){
            return;
        }

        if (session.method() == XMethod.WEBSOCKET) {
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
