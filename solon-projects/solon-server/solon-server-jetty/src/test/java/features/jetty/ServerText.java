package features.jetty;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

@SolonTest(App.class)
public class ServerText extends HttpTester {

    @Test
    public void test() throws Exception {
        assert "hello".equals(path("/test/hello").get());
    }

    @Test
    public void test_old404() throws Exception {
        assert path("/hello").execAsCode("GET") == 404;
    }

    @Test
    public void async() throws Exception {
        assert "async".equals(path("/test/async").get());
    }

    @Test
    public void async_old404() throws Exception {
        assert path("/async").execAsCode("GET") == 404;
    }

    @Test
    public void async_timeout() throws Exception {
        assert 500 == path("/test/async_timeout").head();
    }

    @Test
    public void session() throws Exception {
        MultiMap<String> cookies = new MultiMap<>();
        try (HttpResponse resp = path("/test/session?name=n1").exec("GET")) {
            assert "n1".equals(resp.bodyAsString());

            for (String cookie : resp.cookies()) {
                String[] nameAndValues = cookie.split(";")[0].split("=");
                cookies.add(nameAndValues[0], nameAndValues[1]);
            }
        }

        assert "n1".equals(path("/test/session").cookies(cookies).get());
    }
}