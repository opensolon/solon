package labs;

import demo.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/8/30 created
 */
@SolonTest(App.class)
public class AppTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        assert path("/t1?name=d").get().equals("Hello d");
        assert path("/t2?name=d").get().equals("[\"Hello d\",\"hello2 d\"]");
        assert path("/t2?name=d").header("accept", MimeType.APPLICATION_X_NDJSON_VALUE).get().equals("Hello d\n" +
                "hello2 d");
        assert path("/t3?name=d").get().equals("Hello d\n" +
                "hello2 d");
    }
}
