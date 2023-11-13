package webapp.demoh_socketd;

import org.noear.nami.channel.socketd.SocketdProxy;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping(value = "/demoh/rpc", method = MethodType.ALL)
@Remoting
public class HelloRpcServiceImpl implements HelloRpcService {

    public String hello(String name) {
        Context ctx = Context.current();

        if(MethodType.SOCKET.name.equals(ctx.method())) {
            NameRpcService rpc = SocketdProxy.create(ctx, NameRpcService.class);
            name = rpc.name(name);
        }

        return "name=" + name;
    }
}
