package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2021/6/1 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class PropParseTest {
    @Test
    public void test(){
        assert  "solon.app.group".equals(Solon.cfg().getByParse("solon.app.group"));
        assert  "test".equals(Solon.cfg().getByParse("${solon.app.group}"));
        assert  "test_topic".equals(Solon.cfg().getByParse("${solon.app.group}_topic"));
        assert  "event_test_topic".equals(Solon.cfg().getByParse("event_${solon.app.group}_topic"));
        assert  "event_test".equals(Solon.cfg().getByParse("event_${solon.app.group}"));
    }
}
