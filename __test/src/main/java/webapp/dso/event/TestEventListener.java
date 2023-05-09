package webapp.dso.event;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;

/**
 * @author noear 2023/5/6 created
 */
@Component
public class TestEventListener implements EventListener<TestEvent> {
    @Override
    public void onEvent(TestEvent testEvent) throws Throwable {
        throw new Exception("test");
    }
}
