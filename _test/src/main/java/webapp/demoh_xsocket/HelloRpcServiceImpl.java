package webapp.demoh_xsocket;

import org.noear.fairy.channel.xsocket.XSocket;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping(value = "/demoe/rpc", method = MethodType.SOCKET)
@Component(remoting = true)
public class HelloRpcServiceImpl implements HelloRpcService {
    @Mapping(value = "*", method = MethodType.SOCKET, before = true)
    public void bef(Context ctx) {
        ctx.headerSet("Content-Type", "test/json");
    }

    public String hello(String name) {
        NameRpcService rpc = XSocket.create(Context.current(), NameRpcService.class);

        name = rpc.name(name);

        return "name=" + name;
    }
}
