package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XMonitor;
import org.noear.solonclient.channel.SocketMessage;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.io.PrintWriter;

public class AioProcessor implements MessageProcessor<SocketMessage> {
    @Override
    public void process(AioSession<SocketMessage> session, SocketMessage request) {
        try {
            handle(session, request);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateEvent(AioSession<SocketMessage> session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }

    public void handle(AioSession<SocketMessage> session, SocketMessage request) {
        if (request == null) {
            return;
        }

        AioContext context = new AioContext(session, request);

        try {
            XApp.global().handle(context);
        } catch (Throwable ex) {
            XMonitor.sendError(context,ex);
            ex.printStackTrace(new PrintWriter(context.outputStream()));
        }

        try {
            context.commit();
        } catch (Throwable ex) {
            XMonitor.sendError(context,ex);
        }
    }
}