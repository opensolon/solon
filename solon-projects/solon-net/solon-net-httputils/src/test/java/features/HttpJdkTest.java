package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;

/**
 * @author noear 2024/10/6 created
 */
public class HttpJdkTest {
    static final String url = "http://solon.noear.org";
    static final String url404 = "http://solon.noear.org/_test/_demo";

    public static HttpUtils http(String url){
        return JdkHttpUtilsFactory.getInstance().http(url);
    }

    @Test
    public void get() throws Exception {
        assert http(url)
                .enablePrintln(true)
                .execAsCode("GET") == 200;
    }

    @Test
    public void get_404() throws Exception {
        assert http(url404)
                .enablePrintln(true)
                .execAsCode("GET") == 404;
    }

    @Test
    public void post() throws Exception {
        assert http(url).data("user", "noear")
                .enablePrintln(true)
                .execAsCode("POST") == 200;

        assert http(url).bodyOfJson("{\"user\":\"noear\"}")
                .enablePrintln(true)
                .execAsCode("POST") == 200;

        assert http(url).body("{\"user\":\"noear\"}".getBytes())
                .enablePrintln(true)
                .execAsCode("POST") == 200;
    }

    @Test
    public void post_404() throws Exception {
        assert http(url404).data("user", "noear")
                .enablePrintln(true)
                .execAsCode("POST") == 404;

        assert http(url404).bodyOfJson("{\"user\":\"noear\"}")
                .enablePrintln(true)
                .execAsCode("POST") == 404;

        assert http(url404).body("{\"user\":\"noear\"}".getBytes())
                .enablePrintln(true)
                .execAsCode("POST") == 404;
    }
}
