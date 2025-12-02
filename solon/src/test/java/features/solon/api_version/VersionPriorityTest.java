package features.solon.api_version;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.RoutingTableDefault;

/**
 * 版本优先级测试
 */
public class VersionPriorityTest {
    @Test
    public void testVersionPriorityInRoutingTable() {
        // 创建路由表
        RoutingTableDefault<String> routingTable = new RoutingTableDefault<>();

        // 添加不同版本的路由
        routingTable.add("/api/users",  MethodType.GET, 0,"1.0+","v1.0-handler");
        routingTable.add("/api/users",  MethodType.GET, 0,"2.0","v2.0-handler");
        routingTable.add("/api/users",  MethodType.GET, 0,"1.0","v1.0-exact-handler");
        routingTable.add("/api/users",  MethodType.GET, 0,null,"handler");

        // 测试版本 2.0 的请求 - 应该优先选择 v2.0 而不是 v1.0+
        String result2_0 = routingTable.matchOne("/api/users", "2.0", MethodType.GET);
        assertEquals("v2.0-handler", result2_0, "版本 2.0 应该优先匹配精确的 v2.0 路由");

        // 测试版本 1.1 的请求 - 应该选择 v1.0+
        String result1_1 = routingTable.matchOne("/api/users", "1.1", MethodType.GET);
        assertEquals("v1.0-handler", result1_1, "版本 1.1 应该匹配 v1.0+ 路由");

        // 测试版本 1.0 的请求 - 应该优先选择 v1.0 而不是 v1.0+
        String result1_0 = routingTable.matchOne("/api/users", "1.0", MethodType.GET);
        assertEquals("v1.0-exact-handler", result1_0, "版本 1.0 应该优先匹配精确的 v1.0 路由");


        String result_null = routingTable.matchOne("/api/users", null, MethodType.GET);
        assertEquals("handler", result_null, "无版本 应该优先匹配精确的 无版本 路由");
    }

    @Test
    public void testComplexVersionPriority() {
        RoutingTableDefault<String> routingTable = new RoutingTableDefault<>();

        // 添加多个版本的路由
        routingTable.add("/api/test",  MethodType.GET, 0, "1.0+","v1.0+-handler");
        routingTable.add("/api/test",  MethodType.GET, 0, "1.1+","v1.1+-handler");
        routingTable.add("/api/test",  MethodType.GET, 0, "1.5","v1.5-handler");
        routingTable.add("/api/test",  MethodType.GET, 0, "2.0","v2.0-handler");
        routingTable.add("/api/test",  MethodType.GET, 0,null,"handler");

        // 测试不同版本的优先级选择

        // 版本 2.0 应该选择精确的 v2.0
        assertEquals("v2.0-handler", routingTable.matchOne("/api/test", "2.0", MethodType.GET));

        // 版本 1.5 应该选择精确的 v1.5
        assertEquals("v1.5-handler", routingTable.matchOne("/api/test", "1.5", MethodType.GET));

        // 版本 1.3 应该选择 v1.1+（比 v1.0+ 更精确）
        assertEquals("v1.1+-handler", routingTable.matchOne("/api/test", "1.3", MethodType.GET));

        // 版本 1.0 应该选择 v1.0+
        assertEquals("v1.0+-handler", routingTable.matchOne("/api/test", "1.0", MethodType.GET));
        assertEquals("handler", routingTable.matchOne("/api/test", null, MethodType.GET));
    }

    @Test
    public void testVersionPriorityWithComplexVersionNumbers() {
        RoutingTableDefault<String> routingTable = new RoutingTableDefault<>();

        // 添加复杂版本号的路由
        routingTable.add("/api/complex",  MethodType.GET, 0, "1.0.0","v1.0.0-handler");
        routingTable.add("/api/complex",  MethodType.GET, 0, "1.1.0","v1.1.0-handler");
        routingTable.add("/api/complex",  MethodType.GET, 0, "1.2.0+","v1.2.0+-handler");
        routingTable.add("/api/complex",  MethodType.GET, 0, "2.0.0","v2.0.0-handler");
        routingTable.add("/api/complex",  MethodType.GET, 0,null,"handler");

        // 测试复杂版本号的优先级
        assertEquals("v2.0.0-handler", routingTable.matchOne("/api/complex", "2.0.0", MethodType.GET));
        assertEquals("v1.2.0+-handler", routingTable.matchOne("/api/complex", "1.2.1", MethodType.GET));
        assertEquals("v1.2.0+-handler", routingTable.matchOne("/api/complex", "1.3.0", MethodType.GET));
        assertEquals("v1.1.0-handler", routingTable.matchOne("/api/complex", "1.1.0", MethodType.GET));
        assertEquals("handler", routingTable.matchOne("/api/complex", null, MethodType.GET));
    }
}