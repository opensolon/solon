package demo;

import org.noear.solon.Solon;

/**
 * @author noear 2022/11/21 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);

        System.out.println(Solon.cfg().get("demo.db1.url"));
    }
}
