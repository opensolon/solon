package webapp.demoh_socketd;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.MethodType;

@Mapping(value = "/demoh/rpc/name", method = MethodType.ALL)
@Remoting
public class NameRpcServiceImpl implements NameRpcService{

    @Override
    public String name(String name) {
        return name;
    }
}
