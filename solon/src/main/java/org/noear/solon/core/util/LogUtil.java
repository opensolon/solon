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
package org.noear.solon.core.util;

import org.noear.solon.Solon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志打印小工具（仅限内部使用）
 *
 * @author noear
 * @since 1.10
 * @deprecated 3.6 {@link org.slf4j.Logger}
 * */
@Deprecated
public class LogUtil {
    static final Logger log = LoggerFactory.getLogger(Solon.class);
    private static LogUtil global = new LogUtil();

    public static LogUtil global() {
        return global;
    }

    /**
     * 框架标题
     */
    protected static String title() {
        return "[Solon] ";
    }

    public void trace(String content) {
        log.trace(content);
    }

    public void debug(String content) {
        log.debug(content);
    }

    public void info(String content) {
        log.info(content);
    }

    public void warn(String content) {
        warn(content, null);
    }

    public void warn(String content, Throwable throwable) {
        if (throwable == null) {
            log.warn(content);
        } else {
            log.warn(content, throwable);
        }
    }

    public void error(String content) {
        error(content, null);
    }

    public void error(String content, Throwable throwable) {
        if (throwable == null) {
            log.error(content);
        } else {
            log.error(content, throwable);
        }
    }
}