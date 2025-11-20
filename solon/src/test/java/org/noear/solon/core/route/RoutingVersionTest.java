package org.noear.solon.core.route;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 路由版本匹配测试
 */
public class RoutingVersionTest {

    @Test
    public void testVersionMatching() {
        // 测试精确匹配
        RoutingDefault<String> exactMatch = new RoutingDefault<>("/test", "1.0", 
            org.noear.solon.core.handle.MethodType.GET, "handler1");
        
        assertTrue(exactMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "1.0"));
        assertFalse(exactMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "1.1"));
        assertFalse(exactMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "2.0"));
        
        // 测试模式匹配
        RoutingDefault<String> patternMatch = new RoutingDefault<>("/test", "1.0+", 
            org.noear.solon.core.handle.MethodType.GET, "handler2");
        
        assertTrue(patternMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "1.0"));
        assertTrue(patternMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "1.1"));
        assertTrue(patternMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "1.2"));
        assertTrue(patternMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "1.10"));
        assertTrue(patternMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "2.0"));
        assertFalse(patternMatch.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "0.9"));
        
        // 测试无版本声明
        RoutingDefault<String> noVersion = new RoutingDefault<>("/test", "", 
            org.noear.solon.core.handle.MethodType.GET, "handler3");
        
        assertTrue(noVersion.matches(org.noear.solon.core.handle.MethodType.GET, "/test", ""));
        assertTrue(noVersion.matches(org.noear.solon.core.handle.MethodType.GET, "/test", null));
        assertFalse(noVersion.matches(org.noear.solon.core.handle.MethodType.GET, "/test", "1.0"));
    }
}