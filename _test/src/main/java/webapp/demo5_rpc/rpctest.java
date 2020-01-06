package webapp.demo5_rpc;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solonclient.XProxy;
import org.noear.solonclient.channel.SocketChannel;
import org.noear.solonclient.serializer.SnackSerializer;
import webapp.demoh_socket.SoDemoClientTest;

@XMapping("/demo5/rpctest/")
@XController
public class rpctest implements XHandler {
    @Override
    public void handle(XContext ctx) throws Throwable {

//        String url = "http://localhost:" + XApp.global().port();
        String root = "s://localhost:" + (20000 + XApp.global().port());

        rockapi client = new XProxy()
                .channel(SocketChannel.instance)
                .serializer(SnackSerializer.instance)
                .upstream(name -> root)
                .create(rockapi.class);

        Object val = client.test1(12);
        if (val == null) {
            return;
        }

        ctx.render(val);
    }
}
