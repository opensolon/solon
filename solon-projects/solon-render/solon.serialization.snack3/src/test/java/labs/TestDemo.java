package labs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;


/**
 * @author noear 2021/10/12 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(TestApp.class)
public class TestDemo extends HttpTester {
    @Test
    public void test0() throws Exception{
        String json = path("/t1").get();
        ONode oNode = ONode.loadStr(json);

        assert  oNode.get("time1").getString().length() == 16;
        assert  oNode.get("time2").getString().length() == 10;
        assert  oNode.get("time3").getLong() > 1000000000;
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
