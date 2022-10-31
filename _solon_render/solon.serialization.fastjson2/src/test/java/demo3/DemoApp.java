package demo3;

import org.noear.solon.Solon;
import org.noear.solon.serialization.fastjson2.Fastjson2ActionExecutor;

/**
 * @author noear 2022/10/31 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(demo2.DemoApp.class, args, app -> {
            app.onEvent(Fastjson2ActionExecutor.class, executor -> {
                //executor.config().setLocale();
            });
        });
    }
}
