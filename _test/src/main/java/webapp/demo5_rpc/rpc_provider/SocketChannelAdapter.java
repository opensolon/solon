package webapp.demo5_rpc.rpc_provider;

import org.noear.snack.ONode;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;

import java.util.Map;

public class SocketChannelAdapter implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        if (MethodType.SOCKET.name.equals(ctx.method())) {
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
