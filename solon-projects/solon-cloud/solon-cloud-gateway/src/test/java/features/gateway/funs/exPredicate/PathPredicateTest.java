package features.gateway.funs.exPredicate;

import features.gateway.funs.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.test.SolonTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author noear
 * @since 2.9
 */
@SolonTest
public class PathPredicateTest {
    @Test
    public void testValidConfig() {
        ExPredicate predicate = RouteFactoryManager
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

    @Test
    public void testEmptyConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Path", "");
        });
    }
}
