package demo;

import org.noear.solon.Solon;

/**
 * @author noear 2022/4/20 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            app.onEvent(freemarker.template.Configuration.class, cfg -> {

            });
        });
    }
}
