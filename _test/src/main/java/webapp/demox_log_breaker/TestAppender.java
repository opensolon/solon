package webapp.demox_log_breaker;

import org.noear.solon.logging.LogAbstractAppender;
import org.noear.solon.logging.event.LogEvent;

/**
 * @author noear 2021/2/25 created
 */
public class TestAppender extends LogAbstractAppender {
    @Override
    public String getName() {
        return "test";
    }


    @Override
    protected void appendDo(LogEvent logEvent) {
        System.out.println("[Test] " + logEvent.getContent());
    }
}
