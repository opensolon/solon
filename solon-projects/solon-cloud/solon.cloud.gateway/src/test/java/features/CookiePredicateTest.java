package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;

import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * @author poppoppuppylove
 * @since 2.9
 */
public class CookiePredicateTest {

    @Test
    public void testValidCookieWithPattern() {
        ExPredicate predicate = RouteFactoryManager.global()
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
        ExPredicate predicate = RouteFactoryManager.global()
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
    public void testInvalidCookieConfig() {
        // 测试无效的 Cookie 配置
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.global().getPredicate("Cookie", "invalid_cookie_name,");
        });
    }

    @Test
    public void testEmptyCookieName() {
        // 测试 Cookie 名称为空
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.global().getPredicate("Cookie", ",^pattern");
        });
    }

    @Test
    public void testEmptyRegex() {
        // 测试正则表达式为空
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.global().getPredicate("Cookie", "token,");
        });
    }

}
