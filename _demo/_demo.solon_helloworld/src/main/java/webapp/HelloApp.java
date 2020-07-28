package webapp;

import org.noear.solon.XApp;

/**
 * helloworld 0
 * */
public class HelloApp {
    public static void main(String[] args) {
        XApp app = XApp.start(HelloApp.class, args);

        app.http("/", c -> {
            c.output("hello world!");
//            try {
//                throw new RuntimeException("hello world!");
//            } catch (Exception ex) {
//                c.output(ex.getMessage());
//            }
        });
    }
}
