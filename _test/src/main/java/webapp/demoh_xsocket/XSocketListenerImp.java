package webapp.demoh_xsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XListener;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;
import org.noear.solon.annotation.XServerEndpoint;

@XServerEndpoint(value = "/demoe/websocket")
public class XSocketListenerImp implements XListener {
    @Override
    public void onMessage(XSession session, XMessage message) {
        System.out.println(session.path());

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
