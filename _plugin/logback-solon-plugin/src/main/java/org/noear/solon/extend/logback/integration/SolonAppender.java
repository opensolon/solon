package org.noear.solon.extend.logback.integration;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

import static ch.qos.logback.classic.Level.*;

/**
 * @author noear
 * @since 1.4
 */
public class SolonAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent e) {
        Level level = Level.INFO;

        switch (e.getLevel().toInt()) {
            case TRACE_INT:
                level = Level.TRACE;
                break;
            case DEBUG_INT:
                level = Level.DEBUG;
                break;
            case WARN_INT:
                level = Level.WARN;
                break;
            case ERROR_INT:
                level = Level.ERROR;
                break;
        }

//        IThrowableProxy throwableProxy = e.getThrowableProxy();
//        ThrowableProxyUtil.asString(throwableProxy);

        LogEvent event = new LogEvent(
                e.getLoggerName(),
                level,
                e.getMDCPropertyMap(),
                e.getFormattedMessage(),
                e.getTimeStamp(),
                e.getThreadName(),
                null);

        AppenderManager.getInstance().append(event);
    }
}
