package i18n;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.i18n.LocaleUtil;
import org.noear.solon.test.SolonJUnit4ClassRunner;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * @author noear 2021/7/7 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class MessageTest {
    @Test
    public void test() {
        String text = I18nUtil.getMessage(Solon.cfg().locale(), "login.title");
        System.out.println(Solon.cfg().locale() + ": " + text);

        text = I18nUtil.getMessage(Solon.cfg().locale(), "login.title");
        System.out.println(Solon.cfg().locale() + ": " + text);


        text = I18nUtil.getMessage(Locale.ENGLISH, "login.title");
        assert "login".equals(text);
        System.out.println(Locale.ENGLISH + ": " + text);

        text = I18nUtil.getMessage(Locale.JAPAN, "login.title");
        assert "登录".equals(text);
        System.out.println(Locale.JAPAN + ": " + text);


        text = I18nUtil.getMessage(Locale.US, "login.title");
        assert "login-us".equals(text);
        System.out.println(Locale.US + ": " + text);
    }

    @Test
    public void test2() {
        Map map = I18nUtil.getMessageBundle(Solon.cfg().locale()).toMap();
        assert map.size() == 2;

        System.out.println(map);
    }

    @Test
    public void test3() {
        try {
            I18nUtil.getMessage(Locale.US, "err.text");
        } catch (Throwable e) {
            assert e instanceof MissingResourceException;
            e.printStackTrace();
            return;
        }

        assert false;
    }

    @Test
    public void test4() {
        assert check(Utils.toLocale("zh"), new Locale("zh"));
        assert check(Utils.toLocale("zh_Hant"), new Locale("zh", "", "Hant"));
        assert check(Utils.toLocale("zh_Hant_TW"), new Locale("zh", "Hant", "TW")) == false;


        assert check(Utils.toLocale("zh_Hant") , new Locale("zh", "", "Hant"));
        assert check(Utils.toLocale("zh_Hant_TW") , new Locale("zh", "TW", "Hant"));

        assert check(Utils.toLocale("zh_Hans") , new Locale("zh", "", "Hans"));
        assert check(Utils.toLocale("zh_Hans-CN") , new Locale("zh", "CN", "Hans"));

        assert check(Utils.toLocale("zh_CN") , new Locale("zh", "CN", ""));
        assert check(Utils.toLocale("zh_CN-Hans") , new Locale("zh", "CN", "Hans"));


        assert check(Utils.toLocale("zh-cn") , new Locale("zh", "CN", ""));
        assert check(Utils.toLocale("zh-cn-Hans") , new Locale("zh", "CN", "Hans"));
        assert check(Utils.toLocale("zh-cn_Hans") , new Locale("zh", "CN", "Hans"));
    }

    private boolean check(Locale l1, Locale l2) {
        return l1.getLanguage().equals(l2.getLanguage())
                && l1.getCountry().equals(l2.getCountry())
                && l1.getVariant().equals(l2.getVariant());
    }
}
