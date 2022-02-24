package features.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.dso.cache.OathServer;
import webapp.dso.cache.Oauth;

/**
 * @author noear 2022/1/15 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class CacheTagBuildTest {
    @Inject
    OathServer oathServer;

    @Test
    public void test_by_rst() throws InterruptedException{
        Oauth oauth = oathServer.queryInfoByCode("12");
        Oauth oauth2 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth2.getExpTime());
        assert oauth.getExpTime().equals(oauth2.getExpTime());

        oathServer.updateInfo2(oauth);
        Thread.sleep(1000);

        Oauth oauth3 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth3.getExpTime());
        assert oauth.getExpTime().equals(oauth3.getExpTime()) == false;
    }

    @Test
    public void test_by_param_bean() throws InterruptedException{
        Oauth oauth = oathServer.queryInfoByCode("12");
        Oauth oauth2 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth2.getExpTime());
        assert oauth.getExpTime().equals(oauth2.getExpTime());

        oathServer.updateInfo(oauth);
        Thread.sleep(1000);

        Oauth oauth3 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth3.getExpTime());
        assert oauth.getExpTime().equals(oauth3.getExpTime()) == false;
    }
}
