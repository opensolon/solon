package webapp.demo5_rpc.rpc_provider;

import org.noear.snack.ONode;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.core.XMethod;

import java.util.Map;

public class SocketChannelAdapter implements XHandler {
    @Override
    public void handle(XContext ctx) throws Throwable {
        if (XMethod.SOCKET.name.equals(ctx.method())) {
            String json = ctx.body();
            Map<String, Object> tmp = (Map<String, Object>) ONode.load(json).toData();

            tmp.forEach((k, v) -> {
                if (v != null) {
                    ctx.paramMap().put(k, v.toString());
                }
            });
        }
    }
}
