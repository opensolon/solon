package features;

import demo.App;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.Solon;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

import java.io.IOException;
import java.util.Locale;

/**
 * @author noear 2023/10/10 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class LocalOfCloudTest extends HttpTester {
    @Test
    public void cfg() {
        String tmp = Solon.cfg().get("demo.db1.url");
        System.out.println(tmp);
        assert "tmp".equals(tmp);
    }

    @Test
    public void i18n() {
        String tmp = I18nUtil.getMessage(Locale.CHINA, "user.name");
        System.out.println(tmp);
        assert "java".equals(tmp);
    }

    @Test
    public void ip() throws IOException {
        assert "true".equals(path("/list1").get());
    }
}
