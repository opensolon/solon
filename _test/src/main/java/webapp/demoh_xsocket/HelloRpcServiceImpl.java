package webapp.demoh_xsocket;

import org.noear.fairy.Fairy;
import org.noear.fairy.channel.xsocket.XSocketChannel;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.SnackEncoder;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XSession;

@XMapping(value = "/demoe/rpc", method = XMethod.SOCKET)
@XBean(remoting = true)
public class HelloRpcServiceImpl implements HelloRpcService {
    @XMapping(value = "*", method = XMethod.SOCKET, before = true)
    public void bef(XContext ctx) {
        ctx.headerSet("Content-Type","test/json");
    }

    public String hello(String name) {
//        XContext ctx = XContext.current();
//        XSocketChannel channel = new XSocketChannel((XSession) ctx.request());
//
//        NameRpcService rpc = Fairy.builder()
//                .encoder(SnackEncoder.instance)
//                .decoder(SnackDecoder.instance)
//                .upstream(() -> "tcp://localhost" )
//                .channel(channel)
//                .create(NameRpcService.class);
//
//        String name2 = rpc.name(name);

        return "name=" + name;
    }
}
