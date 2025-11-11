package org.noear.solon.core.util;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.runtime.ClassIndexUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassIndexUtil 测试类
 */
class ClassIndexUtilTest {

    @Test
    void testGetIndexFileName() {
        String basePackage = "com.demo.app";
        String expected = "com-demo-app.solonindex";
        
        assertEquals(expected, ClassIndexUtil.getIndexFileName(basePackage));
    }

    @Test
    void testGetIndexFileNameWithSinglePackage() {
        String basePackage = "app";
        String expected = "app.solonindex";
        
        assertEquals(expected, ClassIndexUtil.getIndexFileName(basePackage));
    }
}