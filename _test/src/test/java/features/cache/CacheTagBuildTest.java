package features.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.dso.cache.OathServer;
import webapp.dso.cache.OathServer2;
import webapp.dso.cache.Oauth;

/**
 * @author noear 2022/1/15 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class CacheTagBuildTest {
    @Inject
    OathServer oathServer;

    @Inject
    OathServer2 oathServer2;

    @Test
    public void test_by_rst() {
        Oauth oauth = oathServer2.queryInfoByCode("12");
        Oauth oauth2 = oathServer2.queryInfoByCode("12");
        assert oauth.getExpTime().equals(oauth2.getExpTime());

        oathServer2.updateInfo2(oauth);

        Oauth oauth3 = oathServer2.queryInfoByCode("12");
        assert oauth.getExpTime().equals(oauth3.getExpTime()) == false;
    }

    @Test
    public void test_by_param_bean() {
        Oauth oauth = oathServer.queryInfoByCode("12");
        Oauth oauth2 = oathServer.queryInfoByCode("12");
        assert oauth.getExpTime().equals(oauth2.getExpTime());

        oathServer.updateInfo(oauth);

        Oauth oauth3 = oathServer.queryInfoByCode("12");
        assert oauth.getExpTime().equals(oauth3.getExpTime()) == false;
    }
}
