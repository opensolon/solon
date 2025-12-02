package features.solon.api_version;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.Version;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 路由版本匹配测试
 */
public class RoutingVersionTest {

    @Test
    public void testVersionExactMatching() {
        // 测试精确匹配
        RoutingDefault<String> exactMatch = new RoutingDefault<String>("/test",
                MethodType.GET, 0).addVersionTarget(new Version("1.0"), "handler1");

        assertTrue(exactMatch.target(new Version("1.0")) != null);
        assertTrue(exactMatch.target(new Version("1.1")) == null);
        assertTrue(exactMatch.target(new Version("2.0")) == null);
    }

    @Test
    public void testVersionPatternMatching() {
        // 测试模式匹配
        RoutingDefault<String> patternMatch = new RoutingDefault<String>("/test", MethodType.GET, 0)
                .addVersionTarget(new Version("1.0+"), "handler2");


        assertTrue(patternMatch.target(new Version("1.0")) != null);
        assertTrue(patternMatch.target(new Version("1.1")) != null);
        assertTrue(patternMatch.target(new Version("1.2")) != null);
        assertTrue(patternMatch.target(new Version("1.10")) != null);
        assertTrue(patternMatch.target(new Version("2.0")) != null);
        assertTrue(patternMatch.target(new Version("0.9")) == null);
    }

    @Test
    public void testVersionNoMatching() {
        // 测试无版本声明
        RoutingDefault<String> noVersion = new RoutingDefault<String>("/test", MethodType.GET, 0)
                .addVersionTarget(null, "handler3");

        assertTrue(noVersion.target(null) != null);
        assertTrue(noVersion.target(new Version("1.0")) == null);
    }
}