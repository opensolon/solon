package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2023/5/6 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class EventTest {
    @Test
    public void test1() {
        EventTest eventTest = new EventTest();

        try {
            EventBus.push(eventTest);
            assert false;
        } catch (Throwable e) {
            e.printStackTrace();
            assert true;
        }
    }
}
