package webapp.demoh_socketd;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.socketd.SocketD;

@Mapping(value = "/demoh/rpc", method = MethodType.ALL)
@Component(remoting = true)
public class HelloRpcServiceImpl implements HelloRpcService {

    public String hello(String name) {
//        NameRpcService rpc = SocketD.create(Context.current(), NameRpcService.class);
//        name = rpc.name(name);

        return "name=" + name;
    }
}
