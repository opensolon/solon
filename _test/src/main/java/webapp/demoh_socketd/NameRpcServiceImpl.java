package webapp.demoh_socketd;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping(value = "/demoh/rpc/name", method = MethodType.ALL)
@Component(remoting = true)
public class NameRpcServiceImpl implements NameRpcService{

    @Override
    public String name(String name) {
        return name;
    }
}
