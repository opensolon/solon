package demo3;

import org.noear.solon.Solon;
import org.noear.solon.serialization.snack3.SnackActionExecutor;

/**
 * @author noear 2022/10/31 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(demo2.DemoApp.class, args, app -> {
            app.onEvent(SnackActionExecutor.class, executor -> {
                executor.config().addDecoder(String.class, (node, type) -> {
                    return node.getString();
                });
            });
        });
    }
}
