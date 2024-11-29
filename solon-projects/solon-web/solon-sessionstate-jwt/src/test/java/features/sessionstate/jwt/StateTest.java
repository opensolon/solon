package features.sessionstate.jwt;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/11/29 created
 */

@SolonTest(App.class)
public class StateTest extends HttpTester {
    @Test
    public void test() throws Exception {
        String sid = "aaaaaaaaaabbbbbbbbbbccccccccccdddddddddd";

        HttpResponse resp = path("/").cookie("SOLONID", sid).exec("GET");
        StateDo rst = resp.bodyAsBean(StateDo.class);
        String token = resp.cookie("TOKEN");
        resp.close();
        System.out.println(rst);

        Thread.sleep(1000);

        StateDo rst2 = path("/")
                .cookie("TOKEN", token)
                .cookie("SOLONID", sid)
                .getAs(StateDo.class);

        System.out.println(rst2);

        assert rst2.t2 > rst2.t1;
        assert rst.t1 == rst2.t1;
    }
}
