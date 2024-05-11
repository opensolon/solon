package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2021/6/1 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class PropParseTest {
    @Test
    public void test(){
        assert  "solon.app.group".equals(Solon.cfg().getByTmpl("solon.app.group"));
        assert  "test".equals(Solon.cfg().getByTmpl("${solon.app.group}"));
        assert  "test_topic".equals(Solon.cfg().getByTmpl("${solon.app.group}_topic"));
        assert  "event_test_topic".equals(Solon.cfg().getByTmpl("event_${solon.app.group}_topic"));
        assert  "event_test".equals(Solon.cfg().getByTmpl("event_${solon.app.group}"));
    }
}
