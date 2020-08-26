package webapp;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.core.XUpstreamFactoryManager;

@XConfiguration
public class Config {
    @XBean
    public XUpstreamFactoryManager upstreamFactory() {
        return (name) -> {
            if ("user-service".equals(name)) {
                return () -> "http://127.0.0.1:8080";
            } else {
                return null;
            }
        };
    }
}
