package features;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.noear.solon.test.AbstractHttpTester;

import java.io.IOException;

/**
 * @author noear 2023/2/14 created
 */
public class MockTest extends AbstractHttpTester {
    public final static String EXPECTED = "{\"status\": \"ok\"}";
    @Rule
    public MockWebServer server = new MockWebServer();

    @Test
    public void test() throws IOException {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        String rst = http(server.getPort()).get();

        assert rst != null;
        assert EXPECTED.equals(rst);
    }
}
