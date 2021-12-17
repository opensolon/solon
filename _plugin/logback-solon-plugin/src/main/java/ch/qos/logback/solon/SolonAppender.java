package ch.qos.logback.solon;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
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

        String message = e.getFormattedMessage();
        IThrowableProxy throwableProxy = e.getThrowableProxy();
        if(throwableProxy != null) {
            String errorStr = ThrowableProxyUtil.asString(throwableProxy);

            if (message.contains("{}")) {
                message = message.replace("{}", errorStr);
            } else {
                message = message + "\n" + errorStr;
            }
        }

        LogEvent event = new LogEvent(
                e.getLoggerName(),
                level,
                e.getMDCPropertyMap(),
                message,
                e.getTimeStamp(),
                e.getThreadName(),
                null);

        AppenderManager.getInstance().append(event);
    }
}
