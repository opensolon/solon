package features.gateway.funs.exPredicate;

import features.gateway.funs.ExContextEmpty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.test.SolonTest;

/**
 * 对请求头的断言测试
 *
 * @author wjc28
 * @since 2.9
 */
@SolonTest
public class HeaderPredicateTest {

    @Test
    public void testEmptyConfig() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Header", "");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Header", null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Header", ",\\d+");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Header", ",");
        });
    }

    @Test
    public void testMatchesHeader() {
        ExPredicate header = RouteFactoryManager.getPredicate("Header", "X-Request-Id, \\d+");
        Assertions.assertNotNull(header);

        boolean test = header.test(new ExContextEmpty() {
            @Override
            public String rawHeader(String key) {
                return "666";
            }
        });
        Assertions.assertTrue(test);
    }


    @Test
    public void testNotMatchesHeader() {
        ExPredicate header = RouteFactoryManager.getPredicate("Header", "X-Request-Id, \\d+");
        Assertions.assertNotNull(header);

        boolean test = header.test(new ExContextEmpty() {
            @Override
            public String rawHeader(String key) {
                return "abcd";
            }
        });
        Assertions.assertFalse(test);
    }
}
