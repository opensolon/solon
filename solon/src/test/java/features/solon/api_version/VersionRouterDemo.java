package features.solon.api_version;

import org.noear.solon.core.handle.MethodType;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.Version;

/**
 * 版本路由匹配演示
 */
public class VersionRouterDemo {

    @Test
    public void demonstrateVersionMatching() {
        System.out.println("=== 路由版本匹配演示 ===");

        // 创建不同版本的路由
        RoutingDefault<String> v1Exact = new RoutingDefault<String>("/api/user", MethodType.GET, 0).addVersionTarget(new Version("1.0"), "Handler for v1.0 exact");
        RoutingDefault<String> v1Plus = new RoutingDefault<String>("/api/user", MethodType.GET, 0).addVersionTarget(new Version("1.0+"), "Handler for v1.0+");
        RoutingDefault<String> v2Exact = new RoutingDefault<String>("/api/user", MethodType.GET, 0).addVersionTarget(new Version("2.0"), "Handler for v2.0 exact");
        RoutingDefault<String> v11Plus = new RoutingDefault<String>("/api/order", MethodType.GET, 0).addVersionTarget(new Version("1.1+"), "Handler for v1.1+");

        // 测试路径: /api/user
        System.out.println("\n--- 测试路径: /api/user ---");

        testRouteMatch(v1Exact, "/api/user", "1.0", "v1.0 精确匹配");
        testRouteMatch(v1Exact, "/api/user", "1.1", "v1.0 精确匹配");
        testRouteMatch(v1Plus, "/api/user", "1.0", "v1.0+ 模式匹配");
        testRouteMatch(v1Plus, "/api/user", "1.1", "v1.0+ 模式匹配");
        testRouteMatch(v1Plus, "/api/user", "1.2", "v1.0+ 模式匹配");
        testRouteMatch(v1Plus, "/api/user", "2.0", "v1.0+ 模式匹配");
        testRouteMatch(v2Exact, "/api/user", "2.0", "v2.0 精确匹配");

        // 测试路径: /api/order
        System.out.println("\n--- 测试路径: /api/order ---");

        testRouteMatch(v11Plus, "/api/order", "1.0", "v1.1+ 模式匹配");
        testRouteMatch(v11Plus, "/api/order", "1.1", "v1.1+ 模式匹配");
        testRouteMatch(v11Plus, "/api/order", "1.2", "v1.1+ 模式匹配");
        testRouteMatch(v11Plus, "/api/order", "2.0", "v1.1+ 模式匹配");

        System.out.println("\n=== 版本号比较测试 ===");

        // 测试版本比较逻辑
        testVersionComparison("1.0", "1.0", 0, "1.0 vs 1.0");
        testVersionComparison("1.1", "1.0", 1, "1.1 vs 1.0");
        testVersionComparison("1.0", "1.1", -1, "1.0 vs 1.1");
        testVersionComparison("1.2.3", "1.2.1", 1, "1.2.3 vs 1.2.1");
        testVersionComparison("2.0", "1.9", 1, "2.0 vs 1.9");
        testVersionComparison("1.10", "1.2", 1, "1.10 vs 1.2");
    }

    private void testRouteMatch(RoutingDefault<String> route, String path, String version, String description) {
        boolean matches = route.target(new Version(version)) != null;
        System.out.printf("%-20s vs %-5s -> %s (%s)%n",
                description, version, matches ? "匹配" : "不匹配", route.target(new Version(version)));
    }

    private void testVersionComparison(String v1, String v2, int expected, String description) {
        // 这里我们需要创建一个临时的 RoutingDefault 来访问私有方法
        // 为了演示，我们使用公共的 matches 方法
        RoutingDefault<String> route = new RoutingDefault<String>("/test", MethodType.GET, 0).addVersionTarget(new Version(v1 + "+"), "test");
        boolean matches = route.target(new Version(v2)) != null;

        // 如果 v1+ 应该匹配 v2，那么 v1 <= v2
        int actualComparison = matches ? (v1.equals(v2) ? 0 : -1) : 1;

        System.out.printf("%-20s -> 比较: %d (期望: %d)%n", description, actualComparison, expected);
    }
}