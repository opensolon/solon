package demo3;

import org.noear.solon.Solon;
import org.noear.solon.schedule.annotation.EnableScheduling;

/**
 * @author noear 2022/6/26 created
 */
@EnableScheduling
public class DemoApp3 {
    public static void main(String[] args){
        Solon.start(DemoApp3.class, args);
    }
}
