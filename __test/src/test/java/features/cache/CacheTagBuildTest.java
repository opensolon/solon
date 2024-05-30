package features.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.cache.OathServer;
import webapp.dso.cache.Oauth;

/**
 * @author noear 2022/1/15 created
 */
@SolonTest(App.class)
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
        Thread.sleep(10);

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
        Thread.sleep(10);

        Oauth oauth3 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth3.getExpTime());
        assert oauth.getExpTime().equals(oauth3.getExpTime()) == false;
    }
}
