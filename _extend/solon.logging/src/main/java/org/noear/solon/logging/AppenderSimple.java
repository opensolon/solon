package org.noear.solon.logging;

import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author noear
 * @since 1.3
 */
public class AppenderSimple implements Appender {
    @Override
    public void append(LogEvent logEvent) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(logEvent.getTimeStamp()).toInstant(), ZoneId.systemDefault());

        StringBuilder buf = new StringBuilder();
        buf.append("[").append(logEvent.getLevel().name()).append("] ");
        buf.append(dateTime.toString()).append(" ");
        buf.append("[-").append(Thread.currentThread().getName()).append("]");

        if (logEvent.getMetainfo() != null) {
            logEvent.getMetainfo().forEach((k, v) -> {
                buf.append("[@").append(k).append(":").append(v).append("]");
            });
        }

        if (logEvent.getInitClass() != null) {
            buf.append(" ").append(logEvent.getInitClass().getTypeName());
        } else {
            buf.append(" ").append(logEvent.getLoggerName());
        }

        buf.append(": ");

        appendTitleDo(buf.toString(), logEvent.getLevel());
        appendContentDo(logEvent.getContent());
    }

    protected void appendTitleDo(String title, Level level) {
        switch (level) {
            case ERROR: {
                PrintUtil.redln(title);
                break;
            }
            case WARN: {
                PrintUtil.yellowln(title);
                break;
            }
            case DEBUG: {
                PrintUtil.blueln(title);
                break;
            }
            case TRACE: {
                PrintUtil.purpleln(title);
                break;
            }
            default: {
                PrintUtil.blackln(title);
                break;
            }
        }
    }

    protected void appendContentDo(Object content) {
        System.out.println(content);
    }
}
