package webapp.demoh_socketd;

import org.noear.nami.channel.socketd.SocketdChannel;
import org.noear.socketd.transport.core.Session;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping(value = "/demoh/rpc", method = MethodType.ALL)
@Remoting
public class HelloRpcServiceImpl implements HelloRpcService {

    public String hello(String name) {
        Context ctx = Context.current();

        if(MethodType.SOCKETD.name.equals(ctx.method())) {
            NameRpcService rpc = SocketdChannel.create((Session) ctx.response(), NameRpcService.class);
            name = rpc.name(name);
        }

        return "name=" + name;
    }
}
