package demo;

import org.noear.solon.Solon;
import org.noear.solon.async.annotation.EnableAsync;

/**
 * @author noear 2022/1/15 created
 */
@EnableAsync
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }
}
