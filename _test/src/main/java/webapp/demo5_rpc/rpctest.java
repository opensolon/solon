package webapp.demo5_rpc;

import org.noear.fairy.Fairy;
import org.noear.fairy.channel.OkHttpChannel;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.SnackEncoder;
import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import webapp.utils.SocketChannel;

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

        rockapi client =  Fairy.builder()
                .channel(OkHttpChannel.instance)
                .decoder(SnackDecoder.instance)
                .upstream(() -> root)
                .create(rockapi.class);

        return client.test1(12);
    }

    private Object socketOf() {
        String root = "s://localhost:" + (20000 + XApp.global().port());

        rockapi client =  Fairy.builder()
                .channel(SocketChannel.instance)
                .encoder(SnackEncoder.instance)
                .decoder(SnackDecoder.instance)
                .upstream(() -> root)
                .create(rockapi.class);

        return client.test1(12);
    }
}
