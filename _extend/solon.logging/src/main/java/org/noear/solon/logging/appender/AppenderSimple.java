package org.noear.solon.logging.appender;

import org.noear.solon.logging.event.AppenderBase;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 简单添加器实现类
 *
 * @author noear
 * @since 1.3
 */
public class AppenderSimple extends AppenderBase {

    /**
     * 是否允许添加
     * */
    protected boolean allowAppend(){
        return true;
    }

    /**
     * 添加日志事件
     *
     * @param logEvent 日志事件
     * */
    @Override
    public void append(LogEvent logEvent) {
        if(allowAppend() == false){
            return;
        }

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

        buf.append(" ").append(logEvent.getLoggerName());

        buf.append("#").append(getName());
        buf.append(": ");

        appendDo(logEvent.getLevel(), buf.toString(), logEvent.getContent());
    }

    protected void appendDo(Level level, String title, Object content) {
        System.out.println(title);
        System.out.println(content);
    }
}
