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
public class CookiePredicateTest {
    @Test
    public void testValidCookieWithPattern() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("Cookie", "token,^user.*");

        assert predicate != null;

        // 测试匹配的 Cookie 值
        assert predicate.test(new ExContextEmpty() {
            @Override
            public String rawCookie(String name) {
                return "user123";  // 符合正则表达式 ^user.*
            }
        });

        // 测试不匹配的 Cookie 值
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public String rawCookie(String name) {
                return "someValue";  // 不符合正则表达式 ^user.*
            }
        });

        // 测试没有 Cookie 的情况
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public String rawCookie(String name) {
                return null;
            }
        });
    }

    @Test
    public void testValidCookieWithoutPattern() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("Cookie", "token");

        assert predicate != null;

        // 测试存在的 Cookie 值
        assert predicate.test(new ExContextEmpty() {
            @Override
            public String rawCookie(String name) {
                return "someValue";
            }
        });

        // 测试不存在的 Cookie
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public String rawCookie(String name) {
                return null;
            }
        });
    }

    @Test
    public void testEmptyCookieName() {
        // 测试 Cookie 名称为空
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Cookie", ",^pattern");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Cookie", ",");
        });
    }
}
