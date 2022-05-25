package features;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;


/**
 * @author noear 2021/10/12 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(TestApp.class)
public class TestDemo extends HttpTestBase {
    @Test
    public void home_test() throws Exception {
        String json = path("/").get();

        JSONObject oNode = JSON.parseObject(json);

        assert oNode.getString("time1").length() == 16;
        assert oNode.getString("time2").length() == 10;
        assert oNode.getLong("time3") > 1000000000;
    }

    @Test
    public void hello_test() throws Exception {
        String json = path("/hello").bodyJson("").post();
        assert "".equals(json);

        json = path("/hello?name=world").bodyJson("").post();
        assert "world".equals(json);

        json = path("/hello").bodyJson("{\"name\":\"world\"}").post();
        assert "world".equals(json);
    }
}
