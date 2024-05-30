package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.gtype.Parent;
import webapp.dso.gtype.S1;
import webapp.dso.gtype.S2;

/**
 * @author noear 2022/1/17 created
 */
@SolonTest(App.class)
public class GtypeTest {
    @Test
    public void test1() {
        Parent o = Solon.context().getBean(S1.class);
        Parent o2 = Solon.context().getBean(S2.class);

        assert 1 == o.hello();
        assert 2 == o2.hello();
    }
}
