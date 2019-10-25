package webapp.demo5_rpc;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solonclient.XProxy;

@XMapping("/demo5/rpctest/")
@XController
public class rpctest implements XHandler {
    @Override
    public void handle(XContext ctx) throws Throwable {
        rockapi client = new XProxy().create(rockapi.class);

        Object val = client.test1(12);
        if(val == null){
            return;
        }

        ctx.render(val);
    }
}
