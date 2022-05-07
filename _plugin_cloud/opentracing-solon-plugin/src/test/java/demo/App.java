package demo;

import org.noear.solon.Solon;
import org.noear.solon.cloud.extend.opentracing.annotation.EnableTracing;

/**
 * @author noear 2022/5/7 created
 */
@EnableTracing
public class App {
    public static void main(String[] args){
        Solon.start(App.class, args);
    }
}
