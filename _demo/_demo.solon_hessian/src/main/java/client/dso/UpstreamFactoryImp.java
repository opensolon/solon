package client.dso;

import org.noear.solon.core.XUpstream;
import org.noear.solon.core.XUpstreamFactory;

public class UpstreamFactoryImp implements XUpstreamFactory {
    private XUpstream test = () -> "http://localhost:8080";

    @Override
    public XUpstream create(String service) {
        return test;
    }
}
