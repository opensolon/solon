package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import reactor.core.publisher.Mono;

/**
 * @author noear 2023/6/19 created
 */
@Controller
public class FluxController {
    @Mapping("/demo2/flux/")
    public Mono<String> flux(String name) throws Exception {
        return Mono.just("Hello " + name);
    }
}
