package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo2_cache.RemotingService;

/**
 * @author noear 2022/3/22 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class CacheRemoting {
    @NamiClient(url = "tcp://localhost:28080/demo2/rmt/sev")
    RemotingService remotingService;

    @Test
    public void cache() throws Exception{
        String tmp = remotingService.hello("noear");
        Thread.sleep(1000);

        String tmp2 = remotingService.hello("noear");

        assert tmp2.equals(tmp);
    }
}
