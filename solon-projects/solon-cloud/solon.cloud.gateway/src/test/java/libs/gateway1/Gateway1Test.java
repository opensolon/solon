package libs.gateway1;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/8/16 created
 */
@SolonTest(Gateway1Main.class)
public class Gateway1Test extends HttpTester {
    @Test
    public void GetTest() throws Exception {
        String rst = path( "/demo/test?name=noear").get();
        assert rst != null;
        assert rst.equals("noear");
    }

    @Test
    public void PostTest() throws Exception {
        String rst = path( "/demo/test").data("name", "noear").post();
        assert rst != null;
        assert rst.equals("noear");
    }

    @Test
    public void PostBodyTest() throws Exception {
        String rst = path( "/demo/test").bodyJson("{\"name\":\"noear\"}").post();
        assert rst != null;
        assert rst.equals("noear");
    }
}
