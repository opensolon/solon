package org.noear.solon.net.http.impl.jdk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.nio.charset.Charset;

/**
 * Tests JdkHttpUtils.urlRebuild() with URLs containing unencoded % characters.
 *
 * @author test
 */
public class UrlRebuildPercentTest {

    private String urlRebuild(String url) throws Exception {
        return ((JdkHttpUtils) JdkHttpUtilsFactory.getInstance().http(url))
                .urlRebuild(null, url, Charset.defaultCharset());
    }

    /**
     * URL with unencoded % in path should not throw IllegalArgumentException.
     */
    @Test
    public void testUnencodedPercent() {
        String url = "http://localhost:8080/50%off";

        String result = Assertions.assertDoesNotThrow(() -> urlRebuild(url));

        System.out.println("testUnencodedPercent result: " + result);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("localhost:8080"));
    }

    /**
     * URL with valid %20 (space) encoding should not throw and should preserve or re-encode properly.
     */
    @Test
    public void testValidPercentEncoding() {
        String url = "http://localhost:8080/path?q=hello%20world";

        String result = Assertions.assertDoesNotThrow(() -> urlRebuild(url));

        System.out.println("testValidPercentEncoding result: " + result);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("localhost:8080/path"));
        // The space should be represented either as %20 or + or actual encoded form
        Assertions.assertTrue(result.contains("hello") && result.contains("world"));
    }

    /**
     * URL with unencoded % in query string should not throw.
     */
    @Test
    public void testPercentInQuery() {
        String url = "http://localhost:8080/path?q=50%off";

        String result = Assertions.assertDoesNotThrow(() -> urlRebuild(url));

        System.out.println("testPercentInQuery result: " + result);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("localhost:8080/path"));
    }

    /**
     * Normal URL without any % characters should work correctly.
     */
    @Test
    public void testNoPercent() {
        String url = "http://localhost:8080/path?q=hello";

        String result = Assertions.assertDoesNotThrow(() -> urlRebuild(url));

        System.out.println("testNoPercent result: " + result);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("http://localhost:8080/path?q=hello", result);
    }
}