package org.noear.mlog;

import org.noear.mlog.utils.LogFormatter;

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
    public void trace(String format, Object[] args) {
        traceDo(null, null, format, args);
    }

    @Override
    public void trace(Marker marker, Object content) {
        traceDo(marker, content, null, null);
    }

    @Override
    public void trace(Marker marker, String format, Object[] args) {
        traceDo(marker, null, format, args);
    }

    private void traceDo(Marker marker, Object content, String format, Object[] args) {
        if (this.isTraceEnabled()) {
            this.writeDo(Level.TRACE, marker, content, format, args);
        }
    }


    @Override
    public void debug(Object content) {
        debugDo(null, content, null, null);
    }

    @Override
    public void debug(String format, Object[] args) {
        debugDo(null, null, format, args);
    }

    @Override
    public void debug(Marker marker, Object content) {
        debugDo(marker, content, null, null);
    }

    @Override
    public void debug(Marker marker, String format, Object[] args) {
        debugDo(marker, null, format, args);
    }

    private void debugDo(Marker marker, Object content, String format, Object[] args) {
        if (this.isDebugEnabled()) {
            this.writeDo(Level.DEBUG, marker, content, format, args);
        }
    }


    @Override
    public void info(Object content) {
        infoDo(null, content, null, null);
    }

    @Override
    public void info(String format, Object[] args) {
        infoDo(null, null, format, args);
    }

    @Override
    public void info(Marker marker, Object content) {
        infoDo(marker, content, null, null);
    }

    @Override
    public void info(Marker marker, String format, Object[] args) {
        infoDo(marker, null, format, args);
    }

    private void infoDo(Marker marker, Object content, String format, Object[] args) {
        if (this.isInfoEnabled()) {
            this.writeDo(Level.INFO, marker, content, format, args);
        }
    }


    @Override
    public void warn(Object content) {
        warnDo(null, content, null, null);
    }

    @Override
    public void warn(String format, Object[] args) {
        warnDo(null, null, format, args);
    }

    @Override
    public void warn(Marker marker, Object content) {
        warnDo(marker, content, null, null);
    }

    @Override
    public void warn(Marker marker, String format, Object[] args) {
        warnDo(marker, null, format, args);
    }

    private void warnDo(Marker marker, Object content, String format, Object[] args) {
        if (this.isWarnEnabled()) {
            this.writeDo(Level.WARN, marker, content, format, args);
        }
    }


    @Override
    public void error(Object content) {
        errorDo(null, content, null, null);
    }

    @Override
    public void error(String format, Object[] args) {
        errorDo(null, null, format, args);
    }

    @Override
    public void error(Marker marker, Object content) {
        errorDo(marker, content, null, null);
    }

    @Override
    public void error(Marker marker, String format, Object[] args) {
        errorDo(marker, null, format, args);
    }

    private void errorDo(Marker marker, Object content, String format, Object[] args) {
        if (this.isErrorEnabled()) {
            this.writeDo(Level.ERROR, marker, content, format, args);
        }
    }


    protected void writeDo(Level level, Marker marker, Object content, String format, Object[] args) {
        if (format != null && format.length() > 0) {
            content = LogFormatter.arrayFormat(format, args);
        }

        append(level, marker, content);
    }

    public void append(Level level, Marker marker, Object content) {
        LoggerFactory.getAppender().append(level, marker, content);
    }
}
