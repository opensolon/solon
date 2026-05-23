package features.enjoy;

import org.noear.solon.annotation.Component;

@Component
public class HelloService {
    public String greet(String name) {
        return "Hi, " + name + "!";
    }
}
