package webapp.demo5_rpc;

import org.noear.fairy.Fairy;
import org.noear.fairy.channel.OkHttpChannel;
import org.noear.nami.channel.xsocket.XSocket;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.util.HashMap;
import java.util.Map;

@Mapping("/demo5/rpctest/")
@Controller
public class rpctest implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        Map<String, Object> map = new HashMap<>();

        map.put("HttpChannel", httpOf());
        map.put("SocketChannel", socketOf());

        ctx.render(map);
    }

    private Object httpOf() {
        String root = "http://localhost:" + Solon.global().port();

        rockapi client = Fairy.builder()
                .channel(OkHttpChannel.instance)
                .decoder(SnackDecoder.instance)
                .upstream(() -> root)
                .create(rockapi.class);

        return client.test1(12);
    }

    private Object socketOf() {
        rockapi client = XSocket.create("localhost", (20000 + Solon.global().port()), rockapi.class);

        return client.test1(12);
    }
}
