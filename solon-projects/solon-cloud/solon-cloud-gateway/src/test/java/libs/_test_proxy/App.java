package libs._test_proxy;

import org.noear.solon.Solon;

/**
 * @author noear 2024/8/23 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, new String[]{"--cfg=_test_proxy.yml"});
    }
}
