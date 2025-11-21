package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.runtime.IndexFileUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IndexFileUtil 测试类
 */
class IndexFileUtilTest {

    @Test
    void testGetIndexFileName() {
        String basePackage = "com.demo.app";
        String expected = "com-demo-app.index";
        
        assertEquals(expected, IndexFileUtil.getIndexFileName(basePackage));
    }

    @Test
    void testGetIndexFileNameWithSinglePackage() {
        String basePackage = "app";
        String expected = "app.index";
        
        assertEquals(expected, IndexFileUtil.getIndexFileName(basePackage));
    }
}