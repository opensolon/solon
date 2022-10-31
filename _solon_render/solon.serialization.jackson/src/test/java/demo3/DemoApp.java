package demo3;

import org.noear.solon.Solon;
import org.noear.solon.serialization.jackson.JacksonActionExecutor;

/**
 * @author noear 2022/10/31 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(demo2.DemoApp.class, args, app -> {
            app.onEvent(JacksonActionExecutor.class, executor -> {
                //executor.config().addHandler();
            });
        });
    }
}
