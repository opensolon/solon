package webapp;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.socketd.handle.SocketdListenerToMvc;

/**
 * @author noear 2023/11/10 created
 */
@ServerEndpoint(schema = "tcp")
public class SocketMvc extends SocketdListenerToMvc {
}
