package features;

import demo.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.web.util.MimeType;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/8/30 created
 */
@SolonTest(value = App.class,args = "--server.port=7071")
public class Case2AppTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        assert path("/case2/m1?name=d").get().equals("Hello d");
    }

    @Test
    public void test2() throws Exception {
        assert path("/case2/f1?name=d").get().equals("[\"Hello d\",\"hello2 d\"]");
    }

    @Test
    public void test3() throws Exception {
        assert path("/case2/f1?name=d").header("accept", MimeType.APPLICATION_X_NDJSON_VALUE).get().equals("Hello d\n" +
                "hello2 d");

    }

    @Test
    public void test4() throws Exception {
        assert path("/case2/f2?name=d").get().equals("Hello d\n" +
                "hello2 d");
    }

    @Test
    public void test5() throws Exception {
        assert path("/case2/f3?name=d").get().equals("[\"Hello d\",\"hello2 d\"]");
    }

    @Test
    public void test6() throws Exception {
        assert 500 == path("/case2/t1?").head();
    }

    @Test
    public void test7() throws Exception {
        assert 200 == path("/case2/t2?").head();
    }
}
