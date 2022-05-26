package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demo2_cache.RemotingService;

/**
 * @author noear 2022/3/22 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
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
