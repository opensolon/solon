package webapp.demo2_cache;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.data.annotation.Cache;

/**
 * @author noear 2022/3/22 created
 */
@Mapping(value = "demo2/rmt/sev", method = MethodType.SOCKET)
@Remoting
public class RemotingServiceImpl implements RemotingService {
    @Cache
    @Override
    public String hello(String name) {
        if (name == null) {
            name = "world";
        }

        return "Hello " + name + ": " + System.currentTimeMillis();
    }
}
