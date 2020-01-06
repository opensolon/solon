package webapp.demo5_rpc.rpc_provider;

import org.noear.snack.ONode;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.core.XMethod;

import java.util.Map;

public class SocketAdp implements XHandler {
    @Override
    public void handle(XContext ctx) throws Throwable {
        if(XMethod.LISTEN.name.equals(ctx.method())){
            String json = ctx.body();
            Map<String,String> tmp = (Map<String, String>) ONode.load(json).toData();

            ctx.paramMap().putAll(tmp);
        }
    }
}
