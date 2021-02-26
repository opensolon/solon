package org.noear.solon.logging;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

/**
 * @author noear
 * @since 1.3
 */
public class LogConsoleAppender extends LogAbstractAppender {

    @Override
    public String getName() {
        return "console";
    }

    @Override
    public Level getDefaultLevel() {
        return Level.TRACE;
    }

    @Override
    protected void appendDo(LogEvent logEvent) {
        if (Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode()) {
            super.appendDo(logEvent);
        }
    }

    @Override
    protected void appendContentDo(Object content) {
        if (content instanceof String) {
            System.out.println(content);
        } else {
            System.out.println(ONode.stringify(content));
        }
    }
}
