package features.undertow;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

@SolonTest(App.class)
public class ServerTest extends HttpTester {

    @Test
    public void test() throws Exception {
        assert "hello".equals(path("/hello").get());
    }

    @Test
    public void async() throws Exception {
        assert "async".equals(path("/async").get());
    }

    @Test
    public void async_timeout() throws Exception {
        assert 500 == path("/async_timeout").head();
    }

    @Test
    public void session() throws Exception {
        MultiMap<String> cookies = new MultiMap<>();
        try (HttpResponse resp = path("/session?name=n1").exec("GET")) {
            assert "n1".equals(resp.bodyAsString());

            for (String cookie : resp.cookies()) {
                String[] nameAndValues = cookie.split(";")[0].split("=");
                cookies.add(nameAndValues[0], nameAndValues[1]);
            }
        }

        assert "n1".equals(path("/session").cookies(cookies).get());
    }

    @Test
    public void ct0() {
        assert path("/ct0").exec("GET").contentType() == null;
    }

    @Test
    public void ct1() {
        assert path("/hello").exec("GET").contentType()
                .startsWith(MimeType.TEXT_PLAIN_VALUE);
    }
}
