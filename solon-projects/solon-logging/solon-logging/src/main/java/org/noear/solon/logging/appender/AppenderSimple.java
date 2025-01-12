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
package org.noear.solon.logging.appender;

import org.noear.solon.Utils;
import org.noear.solon.logging.event.AppenderBase;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
     */
    protected boolean allowAppend() {
        return true;
    }

    /**
     * 添加日志事件
     *
     * @param logEvent 日志事件
     */
    @Override
    public void append(LogEvent logEvent) {
        if (allowAppend() == false) {
            return;
        }

        LocalDateTime dateTime = LocalDateTime.ofInstant(new Date(logEvent.getTimeStamp()).toInstant(), ZoneId.systemDefault());
        DateTimeFormatter dateTimeF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        StringBuilder buf = new StringBuilder();
        buf.append(logEvent.getLevel().name()).append(" ");
        buf.append(dateTimeF.format(dateTime)).append(" ");
        buf.append("#").append(Utils.pid()).append(" ");
        buf.append("[-").append(Thread.currentThread().getName()).append("]");

        if (logEvent.getMetainfo() != null) {
            String traceId = logEvent.getMetainfo().get("traceId");

            if (Utils.isNotEmpty(traceId)) {
                buf.append("[*").append(traceId).append("]");
            }

            logEvent.getMetainfo().forEach((k, v) -> {
                if ("traceId".equals(k) == false) {
                    buf.append("[@").append(k).append(":").append(v).append("]");
                }
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
