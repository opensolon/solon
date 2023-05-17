package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.event.TestEvent;

/**
 * @author noear 2023/5/6 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class EventTest {
    @Test
    public void test1() {
        TestEvent eventTest = new TestEvent();

        try {
            EventBus.push(eventTest);
            assert false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            assert true;
        }
    }
}
