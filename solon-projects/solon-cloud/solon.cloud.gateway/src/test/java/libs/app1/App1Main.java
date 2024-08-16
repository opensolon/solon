package libs.app1;

import org.noear.solon.Solon;

/**
 * @author noear 2024/8/16 created
 */
public class App1Main {
    public static void main(String[] args) {
        Solon.start(App1Main.class, new String[]{"--cfg=app1.yml"});
    }
}
