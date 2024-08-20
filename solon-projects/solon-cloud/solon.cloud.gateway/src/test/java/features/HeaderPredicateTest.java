package features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;

/**
 * @author wjc28
 * @version 1.0
 * @description: 对请求头的断言测试
 * @date 2024-08-20
 */
public class HeaderPredicateTest {

    @Test
    public void testEmptyConfig() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.global().getPredicate("Header", "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.global().getPredicate("Header", null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.global().getPredicate("Header", "X-Request-Id, ");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.global().getPredicate("Header", ",\\d+");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.global().getPredicate("Header", ",");
        });
    }

    @Test
    public void testMatchesHeader(){
        ExPredicate header = RouteFactoryManager.global().getPredicate("Header", "X-Request-Id, \\d+");
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
    public void testNotMatchesHeader(){
        ExPredicate header = RouteFactoryManager.global().getPredicate("Header", "X-Request-Id, \\d+");
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
