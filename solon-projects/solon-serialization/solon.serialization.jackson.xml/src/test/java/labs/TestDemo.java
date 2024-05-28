package labs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        String json = path("/").get();

        JsonNode jsonNode = new XmlMapper().readTree(json);

        assert  jsonNode.get("time1").asText().length() == 16;
        assert  jsonNode.get("time2").asText().length() == 10;
        assert  jsonNode.get("time3").asLong() > 1000000000;
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
