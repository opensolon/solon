package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo5_rpc.RockApi;

/**
 * @author noear 2025/1/18 created
 */
@SolonTest(App.class)
public class RpcTest {
    @Inject
    RockApi rockApi;

    @Test
    public void case1() {
        assert rockApi.test1(1).toString().contains("=1");
    }

    @Test
    public void case6() {
        rockApi.test6(1);
    }
}
