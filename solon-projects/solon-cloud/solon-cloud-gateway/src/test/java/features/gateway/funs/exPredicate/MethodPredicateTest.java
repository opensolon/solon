package features.gateway.funs.exPredicate;

import features.gateway.funs.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.test.SolonTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * @author poppoppuppylove
 * @since 2.9
 */
@SolonTest
public class MethodPredicateTest {

    @Test
    public void testValidMethods() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("Method", "GET,POST");

        assert predicate != null;

        // 测试 GET 方法
        assert predicate.test(new ExContextEmpty() {
            @Override
            public String rawMethod() {
                return "GET";
            }
        });

        // 测试 POST 方法
        assert predicate.test(new ExContextEmpty() {
            @Override
            public String rawMethod() {
                return "POST";
            }
        });

        // 测试 PUT 方法（不在配置中）
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public String rawMethod() {
                return "PUT";
            }
        });

        // 测试 DELETE 方法（不在配置中）
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public String rawMethod() {
                return "DELETE";
            }
        });
    }

    @Test
    public void testInvalidMethodConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Method", "INVALID_METHOD");
        });
    }

    @Test
    public void testEmptyConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Method", "");
        });
    }

    @Test
    public void testNoValidMethods() {
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Method", "UNKNOWN_METHOD");
        });
    }
}
