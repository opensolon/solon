package features;

import demo.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/8/30 created
 */
@SolonTest(value = App.class,args = "--server.port=7071")
public class AppTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        assert path("/t1?name=d").get().equals("Hello d");
    }

    @Test
    public void test2() throws Exception {
        assert path("/t2?name=d").get().equals("[\"Hello d\",\"hello2 d\"]");
    }

    @Test
    public void test3() throws Exception {
        assert path("/t2?name=d").header("accept", MimeType.APPLICATION_X_NDJSON_VALUE).get().equals("Hello d\n" +
                "hello2 d");

    }

    @Test
    public void test4() throws Exception {
        assert path("/t3?name=d").get().equals("Hello d\n" +
                "hello2 d");
    }
}
