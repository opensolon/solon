package webapp;

import org.noear.solon.XApp;

/**
 * helloworld 0
 * */
public class HelloApp {
    public static void main(String[] args) {
        XApp app = XApp.start(HelloApp.class, args);

        app.get("/", c -> c.output("hello world!"));
    }
}
