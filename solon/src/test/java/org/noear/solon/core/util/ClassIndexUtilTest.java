package org.noear.solon.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.noear.solon.core.runtime.NativeDetector.AOT_PROCESSING;

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

    @Test
    void testHasClassIndexForNonExistentPackage() {
        String basePackage = "com.nonexistent.package";
        
        assertFalse(ClassIndexUtil.hasClassIndex(basePackage));
    }

    @Test
    void testLoadClassIndexForNonExistentPackage() {
        String basePackage = "com.nonexistent.package";
        
        assertNull(ClassIndexUtil.loadClassIndex(basePackage));
    }

    @Test
    void testGenerateClassIndexWithNullClassLoader() {
        assertThrows(IllegalArgumentException.class, () -> {
            ClassIndexUtil.generateClassIndex(null, "com.test.package");
        });
    }

    @Test
    void testGenerateClassIndexWithNullPackage() {
        assertThrows(IllegalArgumentException.class, () -> {
            ClassIndexUtil.generateClassIndex(Thread.currentThread().getContextClassLoader(), null);
        });
    }

    @Test
    void testGenerateClassIndexWithEmptyPackage() {
        assertThrows(IllegalArgumentException.class, () -> {
            ClassIndexUtil.generateClassIndex(Thread.currentThread().getContextClassLoader(), "");
        });
    }

    @Test
    void testGenerateClassIndexWithExistPackage() {
        System.setProperty(AOT_PROCESSING, "true");
        assertDoesNotThrow(()->ClassIndexUtil.generateClassIndex(Thread.currentThread().getContextClassLoader(), "demo.solon"));
    }

}