package webapp;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;

/**
 * helloworld 0
 * */
@XSingleton(false)
@XController
public class HelloApp {
    public static void main(String[] args) {
        XApp app = XApp.start(HelloApp.class, args);

//        app.http("/", c -> {
//            c.output("hello world!");
//        });
    }

    @XMapping
    public String hello(){
        return "hello world!";
    }
}
