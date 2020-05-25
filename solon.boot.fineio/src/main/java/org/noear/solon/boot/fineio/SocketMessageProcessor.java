package org.noear.solon.boot.fineio;

import org.noear.fineio.NetSession;
import org.noear.fineio.SessionProcessor;
import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;

import java.io.PrintWriter;


public class SocketMessageProcessor implements SessionProcessor<SocketMessage> {

    @Override
    public void process(NetSession<SocketMessage> session) {
        SocketMessageContext context = new SocketMessageContext(session);
        handle(context);
    }

    public void handle(SocketMessageContext context) {
        try {
            XApp.global().handle(context);
        } catch (Throwable ex) {
            ex.printStackTrace();
            ex.printStackTrace(new PrintWriter(context.outputStream()));
        }

        try {
            context.commit();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
