package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.event.TestEvent;

/**
 * @author noear 2023/5/6 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class EventTest {
    @Test
    public void test1() {
        TestEvent eventTest = new TestEvent();

        try {
            EventBus.publish(eventTest);
            assert false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            assert true;
        }
    }
}
