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
package org.apache.logging.log4j.solon.integration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.fusesource.jansi.AnsiConsole;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.*;
import org.noear.solon.logging.LogIncubator;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.model.LoggerLevelEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 日志孵化器
 *
 * @author noear
 * @since 2.4
 */
public class LogIncubatorImpl implements LogIncubator {
    static final Logger log = LoggerFactory.getLogger(LogIncubatorImpl.class);

    @Override
    public void incubate() throws Throwable {
        if (JavaUtil.IS_WINDOWS && Solon.cfg().isFilesMode() == false) {
            //只在 window 用 jar 模式下才启用
            if (ClassUtil.hasClass(() -> AnsiConsole.class)) {
                try {
                    AnsiConsole.systemInstall();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        //加载pid
        Utils.pid();

        ThreadContext.put("pid", System.getProperty("PID"));

        //尝试从配置里获取
        URL url = getUrlOfConfig();

        //尝试包内定制加载
        if (url == null) {
            //检查是否有原生配置文件
            if (ResourceUtil.hasResource("log4j2.xml")) {
                //如果有直接返回（不支持对它进行 Solon 扩展）
                return;
            }
        }

        //1::尝试应用环境加载
        if (url == null) {
            //尝试环境加载
            if (Utils.isNotEmpty(Solon.cfg().env())) {
                url = ResourceUtil.getResource("log4j2-solon-" + Solon.cfg().env() + ".xml");
            }
        }

        //2::尝试应用加载
        if (url == null) {
            url = ResourceUtil.getResource("log4j2-solon.xml");
        }

        doInit(url);
    }


    protected void doInit(URL url) {
        try {
            doLoadUrl(url);

            //同步 logger level 配置
            if (LogOptions.getLoggerLevels().size() > 0) {
                LoggerContext context = LoggerContext.getContext(false);

                for (LoggerLevelEntity lle : LogOptions.getLoggerLevels()) {
                    LoggerConfig logger = new LoggerConfig(lle.getLoggerExpr(),
                            Level.valueOf(lle.getLevel().name()),
                            true);
                    context.getConfiguration().addLogger(logger.getName(), logger);
                }

                context.updateLoggers();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected void doLoadUrl(URL url) throws Exception {
        if (url == null) {
            //::尝试默认加载
            boolean fileEnable = Solon.cfg().getBool("solon.logging.appender.file.enable", true);

            if (fileEnable) {
                url = ResourceUtil.getResource("META-INF/solon_def/log4j2-def.xml");
            } else {
                url = ResourceUtil.getResource("META-INF/solon_def/log4j2-def_nofile.xml");
            }

            Configurator.reconfigure(url.toURI());
        } else {
            //::加载 xml url
            Configurator.reconfigure(url.toURI());
        }
    }

    /**
     * 基于配置，获取日志配置文件
     */
    private URL getUrlOfConfig() throws MalformedURLException {
        String logConfig = Solon.cfg().get("solon.logging.config");

        if (Utils.isNotEmpty(logConfig)) {
            URL logConfigUrl = ResourceUtil.findResource(logConfig);
            if (logConfigUrl != null) {
                return logConfigUrl;
            } else {
                //改成异步，不然 log 初始化未完成
                RunUtil.async(() -> {
                    log.warn("Props: No log config file: " + logConfig);
                });
            }
        }

        return null;
    }
}
