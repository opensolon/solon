package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.impl.jdk.JdkHttpResponse;

import java.nio.charset.Charset;

/**
 *
 * @author noear 2025/9/22 created
 *
 */
public class CharsetTest {
    @Test
    public void case1() {
        assert JdkHttpResponse.parseContentCharset("type/subtype") == null;
        assert JdkHttpResponse.parseContentCharset("text/html; charset=utf-8") != null;
        assert JdkHttpResponse.parseContentCharset("text/html; charset=utf-8") == Charset.forName("utf-8");
    }
}
