package features.i18n;

import org.junit.Test;
import org.noear.solon.Utils;

import java.util.Locale;

/**
 * @author noear 2021/11/13 created
 */
public class LocaleTest {
    @Test
    public void test1() {
        assert check(Utils.toLocale("zh"), new Locale("zh"));
        assert check(Utils.toLocale("zh_Hant"), new Locale("zh", "", "Hant"));
        assert check(Utils.toLocale("zh_Hant_TW"), new Locale("zh", "Hant", "TW")) == false;


        assert check(Utils.toLocale("zh_Hant"), new Locale("zh", "", "Hant"));
        assert check(Utils.toLocale("zh_Hant_TW"), new Locale("zh", "TW", "Hant"));

        assert check(Utils.toLocale("zh_Hans"), new Locale("zh", "", "Hans"));
        assert check(Utils.toLocale("zh_Hans-CN"), new Locale("zh", "CN", "Hans"));

        assert check(Utils.toLocale("zh_CN"), new Locale("zh", "CN", ""));
        assert check(Utils.toLocale("zh_CN-Hans"), new Locale("zh", "CN", "Hans"));

    }

    @Test
    public void test2() {
        assert check(Utils.toLocale("zh-cn"), new Locale("zh", "CN", ""));
        assert check(Utils.toLocale("zh-cn-Hans"), new Locale("zh", "CN", "Hans"));
        assert check(Utils.toLocale("zh-cn_Hans"), new Locale("zh", "CN", "Hans"));
    }

    private boolean check(Locale l1, Locale l2) {
        return l1.getLanguage().equals(l2.getLanguage())
                && l1.getCountry().equals(l2.getCountry())
                && l1.getVariant().equals(l2.getVariant());
    }
}
