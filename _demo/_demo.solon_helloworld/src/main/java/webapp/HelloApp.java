package webapp;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;

@XController
public class HelloApp {
    public static void main(String[] args) {
        XApp.start(HelloApp.class, args);
    }

    @XMapping("/")
    public String hello(){
        return "hello world!";
    }
}
