package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpMethodTest extends TestBase {

    @Test
    public void test21() throws IOException {
        assert getStatus("/demo2/method/post") == 404;
    }

    @Test
    public void test22() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert post("/demo2/method/post", map).equals("中文");
    }

    @Test
    public void test23() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert http("/demo2/method/put").data(map).put().equals("中文");
    }

    @Test
    public void test23_2() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert http("/demo2/method/delete").data(map).delete().equals("中文");
    }

    @Test
    public void test23_3() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert http("/demo2/method/patch").data(map).patch().equals("中文");
    }

    @Test
    public void test24() throws IOException {
        assert get("/demo2/method/post_get").equals("/demo2/method/post_get");
    }

    @Test
    public void test24_2() throws IOException {
        Map<String, String> map = new HashMap<>();
        assert post("/demo2/method/post_get", map).equals("/demo2/method/post_get");
    }
}
