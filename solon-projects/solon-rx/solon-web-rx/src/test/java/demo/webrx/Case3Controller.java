package demo.webrx;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Entity;
import org.noear.solon.rx.handle.RxEntity;
import reactor.core.publisher.Mono;

/**
 *
 * @author noear 2025/12/16 created
 *
 */
@Mapping("case3")
@Controller
public class Case3Controller {
    @Mapping("m1")
    public Mono<Entity> e1(String name) {
        return RxEntity.ok().body("Hello " + name);
    }

    @Mapping("m2")
    public Mono<Entity> e2(String name) {
        return RxEntity.ok().body(Mono.just("Hello " +  name));
    }
}