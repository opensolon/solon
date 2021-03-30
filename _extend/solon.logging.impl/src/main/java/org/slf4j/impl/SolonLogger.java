package org.slf4j.impl;

import org.noear.solon.Utils;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.0
 */
public class SolonLogger implements Logger {
    private String name;
    private Class<?> initClass;

    public SolonLogger(String name) {
        this.name = name;
        if (name.contains(".")) {
            initClass = Utils.loadClass(name);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return LogOptions.getLevel().code <= Level.TRACE.code;
    }

    @Override
    public void trace(String s) {
        appendDo(Level.TRACE, s, null, null, null);
    }

    @Override
    public void trace(String s, Object o) {
        appendDo(Level.TRACE, null, s, new Object[]{o}, null);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        appendDo(Level.TRACE, null, s, new Object[]{o, o1}, null);
    }

    @Override
    public void trace(String s, Object... objects) {
        appendDo(Level.TRACE, null, s, objects, null);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        appendDo(Level.TRACE, null, s, null, throwable);
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
        return LogOptions.getLevel().code <= Level.DEBUG.code;
    }

    @Override
    public void debug(String s) {
        appendDo(Level.DEBUG, s, null, null, null);
    }

    @Override
    public void debug(String s, Object o) {
        appendDo(Level.DEBUG, null, s, new Object[]{o}, null);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        appendDo(Level.DEBUG, null, s, new Object[]{o, o1}, null);
    }

    @Override
    public void debug(String s, Object... objects) {
        appendDo(Level.DEBUG, null, s, objects, null);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        appendDo(Level.DEBUG, null, s, null, throwable);
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
        return LogOptions.getLevel().code <= Level.INFO.code;
    }

    @Override
    public void info(String s) {
        appendDo(Level.INFO, s, null, null, null);
    }

    @Override
    public void info(String s, Object o) {
        appendDo(Level.INFO, null, s, new Object[]{o}, null);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        appendDo(Level.INFO, null, s, new Object[]{o, o1}, null);
    }

    @Override
    public void info(String s, Object... objects) {
        appendDo(Level.INFO, null, s, objects, null);
    }

    @Override
    public void info(String s, Throwable throwable) {
        appendDo(Level.INFO, null, s, null, throwable);
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
        return LogOptions.getLevel().code <= Level.WARN.code;
    }

    @Override
    public void warn(String s) {
        appendDo(Level.WARN, s, null, null, null);
    }

    @Override
    public void warn(String s, Object o) {
        appendDo(Level.WARN, null, s, new Object[]{o}, null);
    }

    @Override
    public void warn(String s, Object... objects) {
        appendDo(Level.WARN, null, s, objects, null);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        appendDo(Level.WARN, null, s, new Object[]{o, o1}, null);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        appendDo(Level.WARN, null, s, null, throwable);
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
        return LogOptions.getLevel().code <= Level.ERROR.code;
    }

    @Override
    public void error(String s) {
        appendDo(Level.ERROR, s, null, null, null);
    }

    @Override
    public void error(String s, Object o) {
        appendDo(Level.ERROR, null, s, new Object[]{o}, null);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        appendDo(Level.ERROR, null, s, new Object[]{o, o1}, null);
    }

    @Override
    public void error(String s, Object... objects) {
        appendDo(Level.ERROR, null, s, objects, null);
    }

    @Override
    public void error(String s, Throwable throwable) {
        appendDo(Level.ERROR, null, s, null, throwable);
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

    private void appendDo(Level level, Object content, String format, Object[] args, Throwable throwable) {
        Map<String, String> metainfo = MDC.getCopyOfContextMap();

        if (format != null) {
            if (throwable != null) {
                if (args == null || args.length == 0) {
                    args = new Object[]{Utils.throwableToString(throwable)};
                } else {
                    List<Object> list = Arrays.asList(args);
                    list.add(Utils.throwableToString(throwable));
                    args = list.toArray();
                }
            }

            if (args != null && args.length > 0) {
                content = MessageFormatter.arrayFormat(format, args, throwable).getMessage();
            } else {
                content = format;
            }
        }

        LogEvent logEvent = new LogEvent(getName(), initClass, level, metainfo, content,
                System.currentTimeMillis(),
                Thread.currentThread().getName(), throwable);

        AppenderManager.getInstance().append(logEvent);
    }
}
