package features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void test0() throws Exception{
        String json = path("/").get();

        JsonNode oNode = new ObjectMapper().readTree(json);


        assert  oNode.get("time1").asText().length() == 16;
        assert  oNode.get("time2").asText().length() == 10;
        assert  oNode.get("time3").asLong() > 1000000000;
    }
}
