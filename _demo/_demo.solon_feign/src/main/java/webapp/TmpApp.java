package webapp;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XUpstream;
import org.noear.solon.core.XUpstreamFactory;
import org.noear.solon.extend.feign.EnableFeignClients;

@EnableFeignClients
public class TmpApp {
    public static void main(String[] args) {
        XApp.start(TmpApp.class, args);
    }
}
