package features.faas;

import demo.faas.LuffyApp;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2025/7/1 created
 */
@SolonTest(LuffyApp.class)
public class FaasTest extends HttpTester {
    @Test
    public void case1() {
        assert path("/hello.js?name=solon").get().equals("solon");
    }

    @Test
    public void case2() {
        assert path("/hello.js").data("name", "solon").execAsCode("POST") == 405;
    }
}
