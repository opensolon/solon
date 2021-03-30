package org.noear.solon.logging.event;

import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.LogOptions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author noear 2021/2/21 created
 */
public class AppenderSimple implements Appender {
    protected static Appender instance = new AppenderSimple();

    @Override
    public String getName() {
        return "simple";
    }

    @Override
    public void append(LogEvent logEvent) {
        if (LogOptions.getLevel().code > logEvent.getLevel().code) {
            return;
        }

        appendDo(logEvent);
    }

    protected void appendDo(LogEvent logEvent) {

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
            buf.append(" ").append(logEvent.getInitClass().getTypeName()).append("#").append(getName());
        } else {
            buf.append(" ").append(logEvent.getLoggerName()).append("#").append(getName());
        }

        buf.append(":\r\n");

        switch (logEvent.getLevel()) {
            case ERROR: {
                PrintUtil.red(buf.toString());
                break;
            }
            case WARN: {
                PrintUtil.yellow(buf.toString());
                break;
            }
            case DEBUG: {
                PrintUtil.blue(buf.toString());
                break;
            }
            default: {
                PrintUtil.black(buf.toString());
                break;
            }
        }

        appendContentDo(logEvent.getContent());
        if (logEvent.getThrowable() != null) {
            logEvent.getThrowable().printStackTrace();
        }
    }

    protected void appendContentDo(Object content) {
        System.out.println(content);
    }
}
