package demo;

import org.noear.solon.Solon;
import org.noear.solon.extend.quartz.EnableQuartz;

/**
 * @author noear 2022/12/1 created
 */
@EnableQuartz
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }
}
