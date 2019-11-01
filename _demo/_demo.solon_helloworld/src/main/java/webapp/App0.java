package webapp;

import org.noear.solon.XApp;

/**
 * helloworld 0
 * */
public class App0 {
    public static void main(String[] args) {
        XApp app = XApp.start(App1.class, args);

        app.get("/", c -> c.output("hello world!"));
    }
}
