package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;

/**
 * @author noear
 * @since 2.9
 */
public class PathPredicateTest {
    @Test
    public void test1() {
        ExPredicate predicate = RouteFactoryManager.global()
                .getPredicate("Path", "/demo/**");

        assert predicate != null;
        assert predicate.test(new ExContextEmpty() {
            @Override
            public String rawPath() {
                return "/demo/add";
            }
        });
        assert predicate.test(new ExContextEmpty() {
            @Override
            public String rawPath() {
                return "/demo/get/one";
            }
        });
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public String rawPath() {
                return "/user/get/one";
            }
        });
    }
}
