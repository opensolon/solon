package demo;

import org.noear.solon.Solon;
import org.noear.solon.extend.luffy.LuffyHandler;

/**
 * @author noear 2023/11/14 created
 */
public class LuffyApp {
    public static void main(String[] args) {
        Solon.start(LuffyApp.class, args, app -> {
            app.all("**", new LuffyHandler());
        });
    }
}
