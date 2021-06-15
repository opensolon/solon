package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/6/15 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpValidTest2 extends _TestBase{

    @Test
    public void test1() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();

        assert get("/demo2/valid/date?val1=2020-12-12T12:12:12").equals("OK");
    }

}
