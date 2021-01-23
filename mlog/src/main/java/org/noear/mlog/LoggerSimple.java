package org.noear.mlog;

import org.noear.mlog.utils.LogFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志器简化版
 *
 * @author noear
 * @since 1.2
 */
public class LoggerSimple implements Logger {
    protected String name;
    protected Class<?> clz;

    public LoggerSimple(String name) {
        this.name = name;
    }

    public LoggerSimple(Class<?> clz) {
        this.name = clz.getName();
        this.clz = clz;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public void trace(Object content) {
        traceDo(null, content, null, null);
    }

    @Override
    public void trace(String format, Object... args) {
        traceDo(null, null, format, args);
    }

    @Override
    public void trace(Metainfo metainfo, Object content) {
        traceDo(metainfo, content, null, null);
    }

    @Override
    public void trace(Metainfo metainfo, String format, Object... args) {
        traceDo(metainfo, null, format, args);
    }

    private void traceDo(Metainfo metainfo, Object content, String format, Object[] args) {
        if (this.isTraceEnabled()) {
            this.appendDo(Level.TRACE, metainfo, content, format, args);
        }
    }


    @Override
    public void debug(Object content) {
        debugDo(null, content, null, null);
    }

    @Override
    public void debug(String format, Object... args) {
        debugDo(null, null, format, args);
    }

    @Override
    public void debug(Metainfo metainfo, Object content) {
        debugDo(metainfo, content, null, null);
    }

    @Override
    public void debug(Metainfo metainfo, String format, Object... args) {
        debugDo(metainfo, null, format, args);
    }

    private void debugDo(Metainfo metainfo, Object content, String format, Object[] args) {
        if (this.isDebugEnabled()) {
            this.appendDo(Level.DEBUG, metainfo, content, format, args);
        }
    }


    @Override
    public void info(Object content) {
        infoDo(null, content, null, null);
    }

    @Override
    public void info(String format, Object... args) {
        infoDo(null, null, format, args);
    }

    @Override
    public void info(Metainfo metainfo, Object content) {
        infoDo(metainfo, content, null, null);
    }

    @Override
    public void info(Metainfo metainfo, String format, Object... args) {
        infoDo(metainfo, null, format, args);
    }

    private void infoDo(Metainfo metainfo, Object content, String format, Object[] args) {
        if (this.isInfoEnabled()) {
            this.appendDo(Level.INFO, metainfo, content, format, args);
        }
    }


    @Override
    public void warn(Object content) {
        warnDo(null, content, null, null);
    }

    @Override
    public void warn(String format, Object... args) {
        warnDo(null, null, format, args);
    }

    @Override
    public void warn(Metainfo metainfo, Object content) {
        warnDo(metainfo, content, null, null);
    }

    @Override
    public void warn(Metainfo metainfo, String format, Object... args) {
        warnDo(metainfo, null, format, args);
    }

    private void warnDo(Metainfo metainfo, Object content, String format, Object[] args) {
        if (this.isWarnEnabled()) {
            this.appendDo(Level.WARN, metainfo, content, format, args);
        }
    }


    @Override
    public void error(Object content) {
        errorDo(null, content, null, null);
    }

    @Override
    public void error(String format, Object... args) {
        errorDo(null, null, format, args);
    }

    @Override
    public void error(Metainfo metainfo, Object content) {
        errorDo(metainfo, content, null, null);
    }

    @Override
    public void error(Metainfo metainfo, String format, Object... args) {
        errorDo(metainfo, null, format, args);
    }

    private void errorDo(Metainfo metainfo, Object content, String format, Object[] args) {
        if (this.isErrorEnabled()) {
            this.appendDo(Level.ERROR, metainfo, content, format, args);
        }
    }


    protected void appendDo(Level level, Metainfo metainfo, Object content, String format, Object[] args) {
        if (format != null) {
            if (args != null && args.length > 0) {
                content = LogFormatter.arrayFormat(format, args).getMessage();
            } else {
                content = format;
            }
        }

        write(level, metainfo, content);
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void write(Level level, Metainfo metainfo, Object content) {
        String text = null;

        if (metainfo == null) {
            text = String.format("%s [%s] :: %s",
                    sdf.format(new Date()),
                    level.name(),
                    content);
        } else {
            text = String.format("%s [%s] %s:: %s",
                    sdf.format(new Date()),
                    level.name(),
                    metainfo.toString(),
                    content);
        }

        System.out.println(text);
    }
}
