package i18n;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.test.SolonJUnit4ClassRunner;

import java.util.Locale;
import java.util.Map;

/**
 * @author noear 2021/7/7 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class MessageTest {
    @Test
    public void test() {
        String text = I18nUtil.getMessage(Locale.getDefault(), "login.title");
        System.out.println(Locale.getDefault() + "::" + text);


        text = I18nUtil.getMessage(Locale.ENGLISH, "login.title");
        assert "login".equals(text);
        System.out.println(Locale.ENGLISH + "::" + text);

        text = I18nUtil.getMessage(Locale.JAPAN, "login.title");
        assert "登录".equals(text);
        System.out.println(Locale.JAPAN + "::" + text);


        text = I18nUtil.getMessage(Locale.US, "login.title");
        assert "login-us".equals(text);
        System.out.println(Locale.US + "::" + text);
    }

    @Test
    public void test2() {
        Map map = I18nUtil.getMessageBundle(Locale.getDefault()).toMap();
        assert map.size() == 2;

        System.out.println(map);
    }
}
