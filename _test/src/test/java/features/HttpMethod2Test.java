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
public class HttpMethod2Test extends _TestBase {

    @Test
    public void test21() throws IOException {
        assert getStatus("/demo2/method2/post") == 404;
    }

    @Test
    public void test22_post() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert post("/demo2/method2/post", map).equals("中文");
    }

    @Test
    public void test23_head() throws IOException {
        assert path("/demo2/method2/post_get").execAsCode("HEAD") == 200;
    }

    @Test
    public void test23_get() throws IOException {
        assert path("/demo2/method2/post_get").get().equals("/demo2/method2/post_get");
    }

    @Test
    public void test23_post() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/post_get").data(map).post().equals("/demo2/method2/post_get");
    }

    @Test
    public void test24_put() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/put").data(map).put().equals("中文");
    }

    @Test
    public void test24_delete() throws IOException {
        //delete ，有些 server 只支持 queryString param
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/delete").data(map).delete().equals("中文");
    }

    @Test
    public void test24_delete_2() throws IOException {
        assert path("/demo2/method2/delete?name=中文").delete().equals("中文");
    }

    @Test
    public void test24_patch() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/patch").data(map).patch().equals("中文");
    }
}
