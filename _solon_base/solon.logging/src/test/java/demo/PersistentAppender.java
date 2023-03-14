package demo;

import org.noear.solon.logging.event.LogEvent;
import org.noear.solon.logging.persistent.PersistentAppenderBase;

import java.util.List;

/**
 * @author noear 2023/3/14 created
 */
public class PersistentAppender extends PersistentAppenderBase {
    @Override
    public void onEvents(List<LogEvent> list) throws Exception {

    }
}
