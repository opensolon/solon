package org.noear.solon.logging;

import org.noear.snack.ONode;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

/**
 * @author noear
 * @since 1.3
 */
public class LogFileAppender extends LogAbstractAppender {
    public LogFileAppender() {
        setName("file");
        start();
    }

    @Override
    public Level getDefaultLevel() {
        return Level.INFO;
    }

    @Override
    protected void appendDo(LogEvent logEvent) {
        super.appendDo(logEvent);
    }

    @Override
    protected void appendContentDo(Object content) {
        synchronized (System.out) {
            if (content instanceof String) {
                System.out.println(content);
            } else {
                System.out.println(ONode.stringify(content));
            }
        }
    }
}
