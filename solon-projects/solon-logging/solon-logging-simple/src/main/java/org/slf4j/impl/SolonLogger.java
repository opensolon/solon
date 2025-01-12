/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slf4j.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.Map;

/**
 * @author noear
 * @since 1.0
 */
public class SolonLogger implements Logger {
    private String loggerName;
    private Level loggerLevel = Level.TRACE;

    public SolonLogger(String name) {
        loggerName = name;

        if (Solon.app() != null) {
            loggerLevel = LogOptions.getLoggerLevel(name);
        } else {
            EventBus.subscribe(AppInitEndEvent.class, e -> {
                loggerLevel = LogOptions.getLoggerLevel(name);
            });
        }
    }

    @Override
    public String getName() {
        return loggerName;
    }

    @Override
    public boolean isTraceEnabled() {
        return loggerLevel.code <= Level.TRACE.code;
    }

    @Override
    public void trace(String s) {
        appendDo(Level.TRACE, s, null);
    }

    @Override
    public void trace(String s, Object o) {
        appendDo(Level.TRACE, s, new Object[]{o});
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        appendDo(Level.TRACE, s, new Object[]{o, o1});
    }

    @Override
    public void trace(String s, Object... objects) {
        appendDo(Level.TRACE, s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        appendDo(Level.TRACE, s, new Object[]{throwable});
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
        return loggerLevel.code <= Level.DEBUG.code;
    }

    @Override
    public void debug(String s) {
        appendDo(Level.DEBUG, s, null);
    }

    @Override
    public void debug(String s, Object o) {
        appendDo(Level.DEBUG, s, new Object[]{o});
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        appendDo(Level.DEBUG, s, new Object[]{o, o1});
    }

    @Override
    public void debug(String s, Object... objects) {
        appendDo(Level.DEBUG, s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        appendDo(Level.DEBUG, s, new Object[]{throwable});
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
        return loggerLevel.code <= Level.INFO.code;
    }

    @Override
    public void info(String s) {
        appendDo(Level.INFO, s, null);
    }

    @Override
    public void info(String s, Object o) {
        appendDo(Level.INFO, s, new Object[]{o});
    }

    @Override
    public void info(String s, Object o, Object o1) {
        appendDo(Level.INFO, s, new Object[]{o, o1});
    }

    @Override
    public void info(String s, Object... objects) {
        appendDo(Level.INFO, s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        appendDo(Level.INFO, s, new Object[]{throwable});
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
        return loggerLevel.code <= Level.WARN.code;
    }

    @Override
    public void warn(String s) {
        appendDo(Level.WARN, s, null);
    }

    @Override
    public void warn(String s, Object o) {
        appendDo(Level.WARN, s, new Object[]{o});
    }

    @Override
    public void warn(String s, Object... objects) {
        appendDo(Level.WARN, s, objects);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        appendDo(Level.WARN, s, new Object[]{o, o1});
    }

    @Override
    public void warn(String s, Throwable throwable) {
        appendDo(Level.WARN, s, new Object[]{throwable});
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
        return loggerLevel.code <= Level.ERROR.code;
    }

    @Override
    public void error(String s) {
        appendDo(Level.ERROR, s, null);
    }

    @Override
    public void error(String s, Object o) {
        appendDo(Level.ERROR, s, new Object[]{o});
    }

    @Override
    public void error(String s, Object o, Object o1) {
        appendDo(Level.ERROR, s, new Object[]{o, o1});
    }

    @Override
    public void error(String s, Object... objects) {
        appendDo(Level.ERROR, s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        appendDo(Level.ERROR, s, new Object[]{throwable});
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

    private void appendDo(Level level, String messagePattern, Object[] args) {
        if (level.code < loggerLevel.code) {
            return;
        }

        Map<String, String> metainfo = MDC.getCopyOfContextMap();
        Throwable throwable = null;
        String throwableStr = null;


        if(args != null) {
            for (int i = 0, len = args.length; i < len; i++) {
                if (args[i] instanceof Throwable) {
                    throwable = Utils.throwableUnwrap((Throwable) args[i]);
                    throwableStr = Utils.throwableToString(throwable);
                    args[i] = throwableStr;
                    break;
                }
            }
        }

        FormattingTuple tuple = MessageFormatter.arrayFormat(messagePattern, args, throwable);
        String content = tuple.getMessage();

        if (throwableStr != null) {
            //
            // 可能异常不在格式范围内...
            //
            if (Utils.isEmpty(content)) {
                content = throwableStr;
            } else {
                if (throwableStr.length() > content.length()) {
                    content = content + "\n" + throwableStr;
                }
            }
        }


        LogEvent logEvent = new LogEvent(getName(), level, metainfo, content,
                System.currentTimeMillis(),
                Thread.currentThread().getName(), throwable);

        AppenderManager.append(logEvent);
    }
}
