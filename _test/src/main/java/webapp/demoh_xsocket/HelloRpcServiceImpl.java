package webapp.demoh_xsocket;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping(value = "/demoe/rpc", method = MethodType.SOCKET)
@Component(remoting = true)
public class HelloRpcServiceImpl implements HelloRpcService {
    @Mapping(value = "*", method = MethodType.SOCKET, before = true)
    public void bef(Context ctx) {
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
