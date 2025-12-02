package org.noear.solon.core.route;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.noear.solon.core.handle.MethodType;

/**
 * 版本优先级测试
 */
public class VersionPriorityTest {

    @Test
    public void testVersionPriorityInRoutingTable() {
        // 创建路由表
        RoutingTableDefault<String> routingTable = new RoutingTableDefault<>();
        
        // 添加不同版本的路由
        RoutingDefault<String> v1Plus = new RoutingDefault<>("/api/users", "1.0+", MethodType.GET, "v1.0-handler");
        RoutingDefault<String> v2Exact = new RoutingDefault<>("/api/users", "2.0", MethodType.GET, "v2.0-handler");
        RoutingDefault<String> v1Exact = new RoutingDefault<>("/api/users", "1.0", MethodType.GET, "v1.0-exact-handler");
        
        routingTable.add(v1Plus);
        routingTable.add(v2Exact);
        routingTable.add(v1Exact);
        
        // 测试版本 2.0 的请求 - 应该优先选择 v2.0 而不是 v1.0+
        String result2_0 = routingTable.matchOne("/api/users", "2.0", MethodType.GET);
        assertEquals("v2.0-handler", result2_0, "版本 2.0 应该优先匹配精确的 v2.0 路由");
        
        // 测试版本 1.1 的请求 - 应该选择 v1.0+
        String result1_1 = routingTable.matchOne("/api/users", "1.1", MethodType.GET);
        assertEquals("v1.0-handler", result1_1, "版本 1.1 应该匹配 v1.0+ 路由");
        
        // 测试版本 1.0 的请求 - 应该优先选择 v1.0 而不是 v1.0+
        String result1_0 = routingTable.matchOne("/api/users", "1.0", MethodType.GET);
        assertEquals("v1.0-exact-handler", result1_0, "版本 1.0 应该优先匹配精确的 v1.0 路由");
    }

    @Test
    public void testVersionPriorityCalculation() {
        RoutingTableDefault<String> routingTable = new RoutingTableDefault<>();
        
        // 验证版本优先级计算
        String[] versions = {"", "1.0", "1.0+", "2.0", "2.0+"};
        int[] expectedPriorities = {0, 1000000, 1000, 2000000, 2000};
        
        for (int i = 0; i < versions.length; i++) {
            RoutingDefault<String> routing = new RoutingDefault<>("/test", versions[i], MethodType.GET, "handler");
            int priority = routingTable.getVersionPriority(routing);
            System.out.printf("版本 %-8s -> 优先级: %d (期望: %d)%n", 
                "\"" + versions[i] + "\"", priority, expectedPriorities[i]);
        }
    }

    @Test
    public void testComplexVersionPriority() {
        RoutingTableDefault<String> routingTable = new RoutingTableDefault<>();
        
        // 添加多个版本的路由
        RoutingDefault<String> v10Plus = new RoutingDefault<>("/api/test", "1.0+", MethodType.GET, "v1.0+");
        RoutingDefault<String> v11Plus = new RoutingDefault<>("/api/test", "1.1+", MethodType.GET, "v1.1+");
        RoutingDefault<String> v15Exact = new RoutingDefault<>("/api/test", "1.5", MethodType.GET, "v1.5");
        RoutingDefault<String> v20Exact = new RoutingDefault<>("/api/test", "2.0", MethodType.GET, "v2.0");
        
        routingTable.add(v10Plus);
        routingTable.add(v11Plus);
        routingTable.add(v15Exact);
        routingTable.add(v20Exact);
        
        // 测试不同版本的优先级选择
        
        // 版本 2.0 应该选择精确的 v2.0
        assertEquals("v2.0-handler", routingTable.matchOne("/api/test", "2.0", MethodType.GET));
        
        // 版本 1.5 应该选择精确的 v1.5
        assertEquals("v1.5-handler", routingTable.matchOne("/api/test", "1.5", MethodType.GET));
        
        // 版本 1.3 应该选择 v1.1+（比 v1.0+ 更精确）
        assertEquals("v1.1+-handler", routingTable.matchOne("/api/test", "1.3", MethodType.GET));
        
        // 版本 1.0 应该选择 v1.1+（比 v1.0+ 更精确，因为 1.0+ 和 1.1+ 都匹配 1.0，但 1.1+ 版本更高）
        assertEquals("v1.1+-handler", routingTable.matchOne("/api/test", "1.0", MethodType.GET));
    }

    @Test
    public void testVersionPriorityWithComplexVersionNumbers() {
        RoutingTableDefault<String> routingTable = new RoutingTableDefault<>();
        
        // 添加复杂版本号的路由
        RoutingDefault<String> v100 = new RoutingDefault<>("/api/complex", "1.0.0", MethodType.GET, "v1.0.0");
        RoutingDefault<String> v110 = new RoutingDefault<>("/api/complex", "1.1.0", MethodType.GET, "v1.1.0");
        RoutingDefault<String> v120Plus = new RoutingDefault<>("/api/complex", "1.2.0+", MethodType.GET, "v1.2.0+");
        RoutingDefault<String> v200 = new RoutingDefault<>("/api/complex", "2.0.0", MethodType.GET, "v2.0.0");
        
        routingTable.add(v100);
        routingTable.add(v110);
        routingTable.add(v120Plus);
        routingTable.add(v200);
        
        // 测试复杂版本号的优先级
        assertEquals("v2.0.0-handler", routingTable.matchOne("/api/complex", "2.0.0", MethodType.GET));
        assertEquals("v1.2.0+-handler", routingTable.matchOne("/api/complex", "1.2.1", MethodType.GET));
        assertEquals("v1.2.0+-handler", routingTable.matchOne("/api/complex", "1.3.0", MethodType.GET));
        assertEquals("v1.1.0-handler", routingTable.matchOne("/api/complex", "1.1.0", MethodType.GET));
    }
}