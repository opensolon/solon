package webapp.demog_socket;

import org.noear.solon.boot.smartsocket.SsContext;
import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

@SuppressWarnings("unchecked")
public class SsDemoProcessor implements MessageProcessor<SocketMessage> {

    @Override
    public void process(AioSession<SocketMessage> session, SocketMessage request) {
        SsContext ctx = new SsContext(session,request);

        ctx.output("测试");
    }

    @Override
    public void stateEvent(AioSession<SocketMessage> session, StateMachineEnum stateMachineEnum, Throwable throwable) {

    }
}