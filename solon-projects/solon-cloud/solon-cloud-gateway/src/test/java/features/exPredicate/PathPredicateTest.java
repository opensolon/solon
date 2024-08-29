package features.exPredicate;

import features.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author noear
 * @since 2.9
 */
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
