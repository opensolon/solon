package webapp.demoh_socketd;

import org.noear.nami.channel.socketd.SocketdProxy;
import org.noear.socketd.transport.core.impl.ConfigDefault;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.socketd.ToSocketdWebSocketListener;

/**
 * @author noear 2024/5/26 created
 */
@ServerEndpoint("/demoh/rpc/**")
public class WebSocketAsMvc extends ToSocketdWebSocketListener {
    public WebSocketAsMvc() {
        super(new ConfigDefault(false), SocketdProxy.socketdToHandler);
    }
}
