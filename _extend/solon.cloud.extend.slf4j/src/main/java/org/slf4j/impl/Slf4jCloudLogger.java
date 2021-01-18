package org.slf4j.impl;

import org.noear.solon.cloud.model.Level;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
/**
 * @author noear
 * @since 1.2
 */
public class Slf4jCloudLogger implements Logger {
    private String name;

    public Slf4jCloudLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return Slf4jCloudLoggerFactory.INSTANCE.getLevel().code <= Level.TRACE.code;
    }

    @Override
    public void trace(String s) {
        if (isTraceEnabled()) {
            asyncLog(Level.TRACE, s);
        }
    }

    @Override
    public void trace(String s, Object o) {
        if (isTraceEnabled()) {
            asyncLog(Level.TRACE, s, o);
        }
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        if (isTraceEnabled()) {
            asyncLog(Level.TRACE, s, o, o1);
        }
    }

    @Override
    public void trace(String s, Object... objects) {
        if (isTraceEnabled()) {
            asyncLog(Level.TRACE, s, objects);
        }
    }

    @Override
    public void trace(String s, Throwable throwable) {
        if (isTraceEnabled()) {
            asyncLog(Level.TRACE, s, throwable);
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String s) {
        trace(s);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        trace(s, o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        trace(s, o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        trace(s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        trace(s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return Slf4jCloudLoggerFactory.INSTANCE.getLevel().code <= Level.DEBUG.code;
    }

    @Override
    public void debug(String s) {
        if (isDebugEnabled()) {
            asyncLog(Level.DEBUG, s);
        }
    }

    @Override
    public void debug(String s, Object o) {
        if (isDebugEnabled()) {
            asyncLog(Level.DEBUG, s, o);
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if (isDebugEnabled()) {
            asyncLog(Level.DEBUG, s, o, o1);
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        if (isDebugEnabled()) {
            asyncLog(Level.DEBUG, s, objects);
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (isDebugEnabled()) {
            asyncLog(Level.DEBUG, s, throwable);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String s) {
        debug(s, s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        debug(s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        debug(s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        debug(s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        debug(s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return Slf4jCloudLoggerFactory.INSTANCE.getLevel().code <= Level.INFO.code;
    }

    @Override
    public void info(String s) {
        if (isInfoEnabled()) {
            asyncLog(Level.INFO, s);
        }
    }

    @Override
    public void info(String s, Object o) {
        if (isInfoEnabled()) {
            asyncLog(Level.INFO, s, o);
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        if (isInfoEnabled()) {
            asyncLog(Level.INFO, s, o, o1);
        }
    }

    @Override
    public void info(String s, Object... objects) {
        if (isInfoEnabled()) {
            asyncLog(Level.INFO, s, objects);
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        if (isInfoEnabled()) {
            asyncLog(Level.INFO, s, throwable);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String s) {
        info(s);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        info(s, o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        info(s, o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        info(s, objects);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        info(s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return Slf4jCloudLoggerFactory.INSTANCE.getLevel().code <= Level.WARN.code;
    }

    @Override
    public void warn(String s) {
        if (isWarnEnabled()) {
            asyncLog(Level.WARN, s);
        }
    }

    @Override
    public void warn(String s, Object o) {
        if (isWarnEnabled()) {
            asyncLog(Level.WARN, s, o);
        }
    }

    @Override
    public void warn(String s, Object... objects) {
        if (isWarnEnabled()) {
            asyncLog(Level.WARN, s, objects);
        }
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        if (isWarnEnabled()) {
            asyncLog(Level.WARN, s, o, o1);
        }
    }

    @Override
    public void warn(String s, Throwable throwable) {
        if (isWarnEnabled()) {
            asyncLog(Level.WARN, s, throwable);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String s) {
        warn(s);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        warn(s, o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        warn(s, o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        warn(s, objects);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        warn(s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return Slf4jCloudLoggerFactory.INSTANCE.getLevel().code <= Level.ERROR.code;
    }

    @Override
    public void error(String s) {
        if (isErrorEnabled()) {
            asyncLog(Level.ERROR, s);
        }
    }

    @Override
    public void error(String s, Object o) {
        if (isErrorEnabled()) {
            asyncLog(Level.ERROR, s, o);
        }
    }

    @Override
    public void error(String s, Object o, Object o1) {
        if (isErrorEnabled()) {
            asyncLog(Level.ERROR, s, o, o1);
        }
    }

    @Override
    public void error(String s, Object... objects) {
        if (isErrorEnabled()) {
            asyncLog(Level.ERROR, s, objects);
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        if (isErrorEnabled()) {
            asyncLog(Level.ERROR, s, throwable);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String s) {
        error(s);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        error(s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        error(s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        error(s, objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        error(s, throwable);
    }

    private void asyncLog(Level level, String format, Object arg) {
        FormattingTuple formatter = MessageFormatter.format(format, arg);
        asyncLog(level, formatter.getMessage(), formatter.getThrowable());
    }

    private void asyncLog(Level level, String format, Object arg1, Object arg2) {
        FormattingTuple formatter = MessageFormatter.format(format, arg1, arg2);
        asyncLog(level, formatter.getMessage(), formatter.getThrowable());
    }

    private void asyncLog(Level level, String format, Object[] args) {
        FormattingTuple formatter = MessageFormatter.arrayFormat(format, args);
        asyncLog(level, formatter.getMessage(), formatter.getThrowable());
    }

    private void asyncLog(Level level, String msg, Throwable err) {
        if (msg == null && err == null) {
            throw new IllegalArgumentException("both message and error are null");
        }
        StringBuilder msgBuilder = new StringBuilder();
        if (msg != null) {
            msgBuilder.append(msg).append("\n");
        }
        if (err != null) {
            msgBuilder.append(err.toString());
            for (StackTraceElement stackTrace : err.getStackTrace()) {
                msgBuilder.append(stackTrace).append("\n");
            }
        }
        msgBuilder.setLength(msgBuilder.length() - 1);
        asyncLog(level, msgBuilder.toString());
    }

    private void asyncLog(Level level, String content) {
        Slf4jCloudLoggerFactory.INSTANCE.write(level, name, content);
    }
}
