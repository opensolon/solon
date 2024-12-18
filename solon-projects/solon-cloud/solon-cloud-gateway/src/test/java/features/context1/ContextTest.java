package features.context1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/12/18 created
 */
@SolonTest
public class ContextTest extends HttpTester {
    static SimpleSolonApp solonApp;

    @BeforeAll
    public static void bef() throws Throwable {
        solonApp = new SimpleSolonApp(ContextTest.class, "--cfg=context1.yml");
        solonApp.start(null);
    }

    @Test
    public void case1() throws Exception {
        int code = path("/").data("s", "1").execAsCode("GET");
        System.out.println(code);
        assert code == 500;
        assert path("/").data("s", "2").get().contains("{");
        assert path("/").data("s", "3").get().contains("err");
    }

    @Test
    public void case2() throws Exception {
        assert path("/").get().contains("<head>");
    }
}
