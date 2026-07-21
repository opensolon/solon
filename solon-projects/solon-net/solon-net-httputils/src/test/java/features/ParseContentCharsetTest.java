package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.noear.solon.net.http.impl.jdk.JdkHttpResponse;

import java.nio.charset.Charset;

/**
 * Edge-case tests for {@link JdkHttpResponse#parseContentCharset(String)}.
 *
 * @author noear 2025/9/22 created
 */
public class ParseContentCharsetTest {

    @Test
    public void testNull() {
        Assertions.assertNull(JdkHttpResponse.parseContentCharset(null));
    }

    @Test
    public void testNoCharset() {
        Assertions.assertNull(JdkHttpResponse.parseContentCharset("text/html"));
    }

    @Test
    public void testStandardUtf8() {
        Assertions.assertEquals(Charset.forName("utf-8"),
                JdkHttpResponse.parseContentCharset("text/html; charset=utf-8"));
    }

    @Test
    public void testIllegalCharset() {
        Assertions.assertNull(JdkHttpResponse.parseContentCharset("text/html; charset=invalid-charset-xyz"));
    }

    @Test
    public void testAnotherIllegalCharset() {
        Assertions.assertNull(JdkHttpResponse.parseContentCharset("text/html; charset=nonexistent-encoding"));
    }

    @Test
    public void testMultipleParams() {
        Assertions.assertEquals(Charset.forName("utf-8"),
                JdkHttpResponse.parseContentCharset("text/html; charset=utf-8; boundary=something"));
    }

    @Test
    public void testQuotedCharset() {
        Assertions.assertDoesNotThrow(() -> JdkHttpResponse.parseContentCharset("text/html; charset=\"utf-8\""));
    }
}