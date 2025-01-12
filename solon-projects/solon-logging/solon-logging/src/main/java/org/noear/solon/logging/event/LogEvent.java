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
package org.noear.solon.logging.event;

import java.io.Serializable;
import java.util.Map;

/**
 * 日志事件
 *
 * @author noear
 * @since 1.0
 */
public class LogEvent implements Serializable {

    private String loggerName;
    private Level level;
    private Map<String, String> metainfo;
    private Object content;
    private long timeStamp;
    private String threadName;
    private Throwable throwable;

    public LogEvent(String loggerName, Level level, Map<String, String> metainfo, Object content, long timeStamp, String threadName, Throwable throwable) {
        this.loggerName = loggerName;
        this.level = level;
        this.metainfo = metainfo;
        this.content = content;
        this.timeStamp = timeStamp;
        this.threadName = threadName;
        this.throwable = throwable;
    }

    /**
     * 获取日志器名称
     */
    public String getLoggerName() {
        return loggerName;
    }

    /**
     * 获取级别
     */
    public Level getLevel() {
        return level;
    }

    /**
     * 获取元信息
     */
    public Map<String, String> getMetainfo() {
        return metainfo;
    }

    /**
     * 获取内容
     */
    public Object getContent() {
        return content;
    }

    /**
     * 获取时间戳
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * 获取线程名
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * 异常
     * */
    public Throwable getThrowable() {
        return throwable;
    }
}
