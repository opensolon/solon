package i18n;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.i18n.I18nUtils;
import org.noear.solon.test.SolonJUnit4ClassRunner;

import java.util.Locale;

/**
 * @author noear 2021/7/7 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class MessageTest {
    @Test
    public void test() {
        String text = I18nUtils.getMessage(Locale.getDefault(), "login.title");
        System.out.println(Locale.getDefault() + "::" + text);
    }
}
