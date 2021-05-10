package org.noear.solon.logging.appender;

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

        appendDo(logEvent.getLevel(), buf.toString(), logEvent.getContent());
    }

    protected void appendDo(Level level, String title, Object content) {
        System.out.println(title);
        System.out.println(content);
    }
}
