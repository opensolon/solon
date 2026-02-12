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

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.noear.solon.Solon;

/**
 * Log4j2 日志默认配置
 *
 * @author noear
 * @since 3.9.2
 */
public class DefaultLog4j2ConfigFactory {
    static Configuration createConfiguration(ConfigurationBuilder<BuiltConfiguration> builder) {
        boolean fileEnable = Solon.cfg().getBool("solon.logging.appender.file.enable", true);
        boolean consoleEnable = Solon.cfg().getBool("solon.logging.appender.console.enable", true);

        // 1. 全局配置
        builder.setPackages("org.apache.logging.log4j.solon");

        // 2. 提取变量 (模拟 XML 中的 Properties)
        String appName = System.getProperty("solon.app.name", "solon");

        // 3. 创建 Console Appender
        if (consoleEnable) {
            String consolePattern = System.getProperty("solon.logging.appender.console.pattern",
                    "%highlight{%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} #%5X{pid} [-%t][*%X{traceId}]%tags[%logger{20}]:} %n%msg%n");


            AppenderComponentBuilder consoleAppender = builder.newAppender("Console", "Console")
                    .addAttribute("target", "SYSTEM_OUT")
                    .addAttribute("follow", true);
            consoleAppender.add(builder.newLayout("PatternLayout")
                    .addAttribute("pattern", consolePattern)
                    .addAttribute("disableAnsi", false));
            consoleAppender.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
                    .addAttribute("level", System.getProperty("solon.logging.appender.console.level", "TRACE")));
            builder.add(consoleAppender);
        }

        // 4. 创建 RollingFile Appender
        if (fileEnable) {
            String fileLogName = System.getProperty("solon.logging.appender.file.name", "logs/" + appName);
            String maxFileSize = System.getProperty("solon.logging.appender.file.maxFileSize", "10 MB");


            ComponentBuilder<?> policies = builder.newComponent("Policies")
                    .addComponent(builder.newComponent("TimeBasedTriggeringPolicy"))
                    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", maxFileSize));


            AppenderComponentBuilder fileAppender = builder.newAppender("File", "RollingFile")
                    .addAttribute("fileName", fileLogName + ".log")
                    .addAttribute("filePattern", fileLogName + "_%d{yyyy-MM-dd}_%i.log");
            fileAppender.add(builder.newLayout("PatternLayout")
                    .addAttribute("pattern", System.getProperty("solon.logging.appender.file.pattern", "%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} #%5X{pid} [-%t][*%X{traceId}]%tags[%logger{20}]: %n%msg%n")));
            fileAppender.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
                    .addAttribute("level", System.getProperty("solon.logging.appender.file.level", "INFO")));
            fileAppender.addComponent(policies);
            fileAppender.addComponent(builder.newComponent("DefaultRolloverStrategy")
                    .addAttribute("max", System.getProperty("solon.logging.appender.file.maxHistory", "7")));
            builder.add(fileAppender);
        }

        // 5. 创建 Solon 自定义 Appender
        AppenderComponentBuilder solonAppender = builder.newAppender("Solon", "Solon");
        solonAppender.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
                .addAttribute("level", "TRACE"));
        builder.add(solonAppender);

        // 6. 配置 Root Logger
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(System.getProperty("solon.logging.logger.root.level", "TRACE"));

        if (consoleEnable) {
            rootLogger.add(builder.newAppenderRef("Console"));
        }

        if (fileEnable) {
            rootLogger.add(builder.newAppenderRef("File"));
        }

        rootLogger.add(builder.newAppenderRef("Solon"));
        builder.add(rootLogger);

        return builder.build();
    }
}