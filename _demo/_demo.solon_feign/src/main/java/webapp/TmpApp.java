package webapp;

import org.noear.solon.XApp;
import org.noear.solon.extend.feign.EnableFeignClients;

@EnableFeignClients
public class TmpApp {
    public static void main(String[] args) {
        XApp.start(TmpApp.class, args);
    }
}
