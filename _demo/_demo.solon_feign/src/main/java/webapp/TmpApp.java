package webapp;

import org.noear.solon.XApp;
import org.noear.solon.core.XBridge;
import org.noear.solon.extend.feign.EnableFeignClient;

@EnableFeignClient
public class TmpApp {
    public static void main(String[] args) {
        XApp.start(TmpApp.class, args, (app) -> {
            XBridge.upstreamFactorySet((name) -> {
                if ("user-service".equals(name)) {
                    return () -> "http://127.0.0.1:8080";
                } else {
                    return null;
                }
            });
        });
    }
}
