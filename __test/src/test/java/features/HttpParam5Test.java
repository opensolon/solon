package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2021/1/5 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class HttpParam5Test extends HttpTester {
    @Test
    public void test1() throws Exception {
        String rst = path("/demo2/param5/test1?a=1&params[a]=2").get();

        assert "1:2".equals(rst);
    }

    @Test
    public void test2() throws Exception {
        String rst = path("/demo2/param5/test2?cat=2").get();

        assert "demo2".equals(rst);

        rst = path("/demo2/param5/test2?cat=1").get();

        assert "demo1".equals(rst);
    }

    @Test
    public void test3() throws Exception {
        String rst = path("/demo2/param5/test3?cat=2").get();

        assert "demo2".equals(rst);

        rst = path("/demo2/param5/test3?cat=1").get();

        assert "demo1".equals(rst);
    }

    private static final String test4_rst1 = "{\"a\":[\"1\"],\"b\":[\"2\"]}";
    private static final String test4_rst2 = "{\"b\":[\"2\"],\"a\":[\"1\"]}";

    @Test
    public void test4() throws Exception {
        String rst = path("/demo2/param5/test4?a=1").data("b", "2").post();
        assert test4_rst1.equals(rst) || test4_rst2.equals(rst);

        rst = path("/demo2/param5/test4?a=1").data("b", "2").multipart(true).post();
        assert test4_rst1.equals(rst) || test4_rst2.equals(rst);
    }

    @Test
    public void test4_2() throws Exception {
        String rst = path("/demo2/param5/test4?a=1").data("b", "2").delete();
        assert test4_rst1.equals(rst) || test4_rst2.equals(rst);

        rst = path("/demo2/param5/test4?a=1").data("b", "2").multipart(true).delete();
        assert test4_rst1.equals(rst) || test4_rst2.equals(rst);
    }
}
