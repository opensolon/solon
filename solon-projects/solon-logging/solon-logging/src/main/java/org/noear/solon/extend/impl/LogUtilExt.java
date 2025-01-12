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
package org.noear.solon.extend.impl;

import org.noear.solon.Solon;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.LogIncubator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/**
 * 把内核日志转到 Slf4j 接口
 *
 * @author noear
 * @since 2.3
 */
public class LogUtilExt extends LogUtil {
    static final Logger log = LoggerFactory.getLogger(Solon.class);

    public LogUtilExt() {
        if (Solon.app() != null) {
            incubate();
        }
    }

    /**
     * 孵化日志实现（加载配置，转换格式）
     */
    private void incubate() {
        ServiceLoader<LogIncubator> internetServices = ServiceLoader.load(LogIncubator.class);
        for (LogIncubator logIncubator : internetServices) {
            try {
                logIncubator.incubate();
            } catch (Throwable e) {
                // native: 静态扩展，初始化 LogUtilExt 报错时，此处可以将异常打印出来
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
            break;
        }
    }

    @Override
    public void trace(String content) {
        log.trace(content);
    }

    @Override
    public void debug(String content) {
        log.debug(content);
    }

    @Override
    public void info(String content) {
        log.info(content);
    }

    @Override
    public void warn(String content, Throwable throwable) {
        if (throwable == null) {
            log.warn(content);
        } else {
            log.warn(content, throwable);
        }
    }

    @Override
    public void error(String content, Throwable throwable) {
        if (throwable == null) {
            log.error(content);
        } else {
            log.error(content, throwable);
        }
    }
}
