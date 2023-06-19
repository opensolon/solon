package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import reactor.core.publisher.Flux;

/**
 * @author noear 2023/6/19 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }

    @Mapping("/")
    public Flux hello(String name) {
        return Flux.just("Hello " + name);
    }
}
