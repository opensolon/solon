package features.enjoy;

import org.noear.solon.annotation.Component;

@Component("share:greeting")
public class Greeting {
    public String greet(String name) {
        return "Hi, " + name + "!";
    }
}
