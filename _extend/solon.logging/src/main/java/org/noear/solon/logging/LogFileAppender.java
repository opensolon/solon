package org.noear.solon.logging;

import org.noear.snack.ONode;
import org.noear.solon.logging.event.Level;

/**
 * file appender
 *
 * @author noear
 * @since 1.3
 */
public class LogFileAppender extends AppenderSimple {
    @Override
    public Level getDefaultLevel() {
        return Level.INFO;
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
