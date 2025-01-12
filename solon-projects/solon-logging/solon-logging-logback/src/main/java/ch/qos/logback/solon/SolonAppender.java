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
 * @since 1.10
 */
public class SolonAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent e) {
        //solon console 1个，所以最少2个起
        if (AppenderManager.count() < 2) {
            return;
        }

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
        if (throwableProxy != null) {
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


        AppenderManager.appendNotPrinted(event);
    }
}
