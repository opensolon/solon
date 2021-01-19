package webapp.demo5_rpc;

import org.noear.solon.core.LoadBalance;

public class UserUpstreamFactory implements LoadBalance.Factory {
    @Override
    public LoadBalance create(String group, String service) {
        return null;
    }
}
