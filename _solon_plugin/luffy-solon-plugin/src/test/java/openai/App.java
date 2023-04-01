package openai;

import org.noear.solon.Solon;
import org.noear.solon.extend.luffy.LuffyHandler;

/**
 * @author noear 2023/4/1 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            app.all("**", new LuffyHandler());
        });
    }
}
