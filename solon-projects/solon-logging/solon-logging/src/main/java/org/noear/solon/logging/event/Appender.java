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


/**
 * 日志添加器
 *
 * @author noear
 * @since 1.0
 */
public interface Appender {
    /**
     * 默认级别
     * */
    default Level getDefaultLevel() {
        return Level.TRACE;
    }

    /**
     * 开始生命周期（一般由 AppenderHolder 控制 ）
     *
     * @see org.noear.solon.logging.AppenderHolder
     * */
    default void start(){}

    /**
     * 停止生命周期
     * */
    default void stop(){}

    /**
     * 获取名称
     * */
    String getName();
    /**
     * 设置名称
     *
     * @param name 名称
     * */
    void setName(String name);

    /**
     * 添加日志事件
     *
     * @param logEvent 日志事件
     * */
    void append(LogEvent logEvent);
}
