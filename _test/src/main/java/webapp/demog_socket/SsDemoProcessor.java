package webapp.demog_socket;

import org.noear.solon.core.SocketMessage;
import org.noear.solon.ext.Act1;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class SsDemoProcessor implements MessageProcessor<SocketMessage> {

    public AioSession<SocketMessage> session;
    private Map<String,Act1<SocketMessage>> msgCallback = new HashMap<>();

    @Override
    public void process(AioSession<SocketMessage> session, SocketMessage msg) {
        if (msg == null) {
            return;
        }

        Act1<SocketMessage> act = msgCallback.remove(msg.key);
        if (act != null) {
            act.run(msg);
        }
    }

    @Override
    public void stateEvent(AioSession<SocketMessage> session, StateMachineEnum stateMachineEnum, Throwable throwable) {
        switch (stateMachineEnum) {
            case NEW_SESSION:
                this.session = session;
                break;

            case SESSION_CLOSED:
                this.session = null;
                msgCallback.clear();
                break;
        }

        System.out.println(stateMachineEnum.name());
    }

    public void send(String path, String message, Act1<SocketMessage> callback) throws IOException {
        SocketMessage msg = new SocketMessage(UUID.randomUUID().toString(),path,message);

        msgCallback.put(msg.key, callback);

        session.writeBuffer().writeAndFlush(msg.wrap().array());
    }
}