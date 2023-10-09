package features;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

import java.util.Map;


/**
 * @author noear 2021/10/12 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(TestApp.class)
public class TestDemo extends HttpTester {
    @Test
    public void test0() throws Exception{
        String json = path("/").get();

        Map oNode = new Gson().fromJson(json, Map.class);

        assert  ((String)oNode.get("time1")).length() == 16;
        assert  ((String)oNode.get("time2")).length() == 10;
        assert  ((Number)oNode.get("time3")).longValue() > 1000000000;
    }
}
