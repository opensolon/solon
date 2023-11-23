package webapp2;

import org.noear.solon.Solon;

/**
 * @author noear 2023/11/23 created
 */
public class App2 {
    public static void main(String[] args) {
        Solon.start(App2.class, args, app -> {
            app.enableWebSocket(true);
        });
    }
}
