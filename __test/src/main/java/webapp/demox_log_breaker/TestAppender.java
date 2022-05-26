package webapp.demox_log_breaker;

import org.noear.solon.logging.event.AppenderBase;
import org.noear.solon.logging.event.LogEvent;

/**
 * @author noear 2021/2/25 created
 */
public class TestAppender extends AppenderBase {
    @Override
    public void append(LogEvent logEvent) {
        System.out.println("[Test] " + logEvent.getContent());
    }
}
