package webapp.demoh_xsocket;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping(value = "/demoe/rpc/name", method = MethodType.SOCKET)
@Component(remoting = true)
public class NameRpcServiceImpl implements NameRpcService{
    @Mapping(value = "*", method = MethodType.SOCKET, before = true)
    public void bef(Context ctx) {
        ctx.headerSet("Content-Type","test/json");
    }

    @Override
    public String name(String name) {
        return name;
    }
}
