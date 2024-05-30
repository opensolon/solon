package labs.labs1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;


/**
 * @author noear 2021/10/12 created
 */
@SolonTest(TestApp.class)
public class TestDemo extends HttpTester {
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
