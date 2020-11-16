package webapp;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.extend.feign.EnableFeignClient;

@EnableFeignClient
public class TmpApp {
    public static void main(String[] args) {
        Solon.start(TmpApp.class, args, (app) -> {
            Bridge.upstreamFactorySet((name) -> {
                if ("user-service".equals(name)) {
                    return () -> "http://127.0.0.1:8080";
                } else {
                    return null;
                }
            });
        });
    }
}
