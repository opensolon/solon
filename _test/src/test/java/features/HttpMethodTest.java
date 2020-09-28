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
public class HttpMethodTest extends TestBase{

    @Test
    public void test25() throws IOException {
        assert  getStatus("/demo2/method/post") == 404;
    }

    @Test
    public void test25_2() throws IOException{
        Map<String,String> map = new HashMap<>();
        map.put("name","中文");

        assert  post("/demo2/method/post",map).equals("中文");
    }

    @Test
    public void test25_3() throws IOException{
        Map<String,String> map = new HashMap<>();
        map.put("name","中文");

        assert  put("/demo2/method/put",map).equals("中文");
    }

    @Test
    public void test26() throws IOException{
        assert  get("/demo2/mapping/post_get").equals("/demo2/mapping/post_get");
    }
}
