package features.i18n;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nService;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.Locale;

/**
 * @author noear 2021/9/19 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class I18nUtilTest {
    @Test
    public void test() {
        assert "登录".equals(I18nUtil.getMessage(Locale.CHINA, "login.title"));
        assert "login-us".equals(I18nUtil.getMessage(Locale.US, "login.title"));
    }

    @Test
    public void test1() {
        I18nBundle bundle = I18nUtil.getMessageBundle(Locale.CHINA);
        assert "登录".equals(bundle.get("login.title"));
    }

    I18nService service = new I18nService(I18nUtil.getMessageBundleName());

    @Test
    public void test2() {
        assert "登录".equals(service.get(Locale.CHINA, "login.title"));
        assert "login-us".equals(service.get(Locale.US, "login.title"));
    }
}
