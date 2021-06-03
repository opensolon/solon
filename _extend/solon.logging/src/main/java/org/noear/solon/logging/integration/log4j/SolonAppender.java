package org.noear.solon.logging.integration.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;


/**
 * @author noear 2021/6/3 created
 */
public class SolonAppender extends AppenderSkeleton {

    @Override
    protected void append(LoggingEvent e) {
        Level level = Level.INFO;

        switch (e.getLevel().toInt()) {
            case 5000:
                level = Level.TRACE;
                break;
            case Priority.DEBUG_INT:
                level = Level.DEBUG;
                break;
            case Priority.WARN_INT:
                level = Level.WARN;
                break;
            case Priority.ERROR_INT:
            case Priority.FATAL_INT:
                level = Level.ERROR;
                break;
        }

        ThrowableInformation tmp = e.getThrowableInformation();

        LogEvent event = new LogEvent(
                e.getLoggerName(),
                level,
                e.getProperties(),
                e.getMessage(),
                e.getTimeStamp(),
                e.getThreadName(),
                (tmp == null ? null : tmp.getThrowable()));

        AppenderManager.getInstance().append(event);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
