package labs;

import org.noear.solon.Solon;

/**
 * @author noear 2024/8/23 created
 */
public class MainTest {
    public static void main(String[] args) {
        Solon.start(MainTest.class, args, app -> {
            app.get("/", c -> c.output("Hello!"));
        });
    }
}
