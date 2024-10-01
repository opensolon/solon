package features.undertow;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2024/10/1 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(ServerText.class, args);
    }

    @Mapping("hello")
    public String hello() {
        return "hello";
    }
}
