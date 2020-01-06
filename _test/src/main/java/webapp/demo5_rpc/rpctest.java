package webapp.demo5_rpc;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solonclient.XProxy;
import org.noear.solonclient.channel.HttpChannel;
import org.noear.solonclient.channel.SocketChannel;
import org.noear.solonclient.serializer.SnackSerializer;
import webapp.demoh_socket.SoDemoClientTest;

import java.util.HashMap;
import java.util.Map;

@XMapping("/demo5/rpctest/")
@XController
public class rpctest implements XHandler {
    @Override
    public void handle(XContext ctx) throws Throwable {
        Map<String, Object> map = new HashMap<>();

        map.put("HttpChannel", httpOf());
        map.put("SocketChannel", socketOf());

        ctx.render(map);
    }

    private Object httpOf() {
        String root = "http://localhost:" + XApp.global().port();

        rockapi client = new XProxy()
                .channel(HttpChannel.instance)
                .serializer(SnackSerializer.instance)
                .upstream(name -> root)
                .create(rockapi.class);

        return client.test1(12);
    }

    private Object socketOf() {
        String root = "s://localhost:" + (20000 + XApp.global().port());

        rockapi client = new XProxy()
                .channel(SocketChannel.instance)
                .serializer(SnackSerializer.instance)
                .upstream(name -> root)
                .create(rockapi.class);

        return client.test1(12);
    }
}
