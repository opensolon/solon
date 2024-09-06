package features.exPredicate;

import features.ExContextEmpty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;

/**
 * 对请求头的断言测试
 *
 * @author wjc28
 * @since 2.9
 */
public class QueryPredicateTest {

    @Test
    public void testEmptyConfig() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Query", "");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Query", null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Query", ",\\d+");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Query", ",");
        });
    }

    @Test
    public void testMatchesHeader() {
        ExPredicate header = RouteFactoryManager.getPredicate("Query", "X-Request-Id, \\d+");
        Assertions.assertNotNull(header);

        boolean test = header.test(new ExContextEmpty() {
            @Override
            public String rawQueryParam(String key) {
                return "666";
            }
        });
        Assertions.assertTrue(test);
    }


    @Test
    public void testNotMatchesHeader() {
        ExPredicate header = RouteFactoryManager.getPredicate("Query", "X-Request-Id, \\d+");
        Assertions.assertNotNull(header);

        boolean test = header.test(new ExContextEmpty() {
            @Override
            public String rawQueryParam(String key) {
                return "abcd";
            }
        });
        Assertions.assertFalse(test);
    }
}
