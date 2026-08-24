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

import java.util.regex.Matcher;

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
            String totalSizeCap = System.getProperty("solon.logging.appender.file.totalSizeCap", "0");


            ComponentBuilder<?> policies = builder.newComponent("Policies")
                    .addComponent(builder.newComponent("TimeBasedTriggeringPolicy"))
                    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", maxFileSize));


            String filePattern = fileLogName + "_%d{yyyy-MM-dd}_%i.log";

            AppenderComponentBuilder fileAppender = builder.newAppender("File", "RollingFile")
                    .addAttribute("fileName", fileLogName + ".log")
                    .addAttribute("filePattern", filePattern);
            fileAppender.add(builder.newLayout("PatternLayout")
                    .addAttribute("pattern", System.getProperty("solon.logging.appender.file.pattern", "%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} #%5X{pid} [-%t][*%X{traceId}]%tags[%logger{20}]: %n%msg%n")));
            fileAppender.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
                    .addAttribute("level", System.getProperty("solon.logging.appender.file.level", "INFO")));
            fileAppender.addComponent(policies);
            ComponentBuilder<?> rolloverStrategy = builder.newComponent("DefaultRolloverStrategy")
                    .addAttribute("max", System.getProperty("solon.logging.appender.file.maxHistory", "7"));

            if (parseSizeBytes(totalSizeCap) > 0) {
                // 模拟 logback 的 totalSizeCap：超过总大小时，滚动并删除最旧的文件
                String fileName = fileLogName.replaceAll("^.*[\\/]", "");
                // 清理范围依据 filePattern 自动推导：basePath 取目录链最顶层，maxDepth 取目录层级数
                String dirPart = filePattern.replaceAll("[\\/][^\\/]*$", "");
                String basePath;
                int maxDepth;
                
                if (dirPart.equals(filePattern)) {
                    // filePattern 无目录部分，清理当前目录
                    basePath = ".";
                    maxDepth = 1;
                } else {
                    String[] dirSegments = dirPart.split("[\\/]+");

                    // 过滤空段（绝对路径打头产生的空段、连续分隔符等）
                    java.util.List<String> segments = new java.util.ArrayList<>();
                    for (String seg : dirSegments) {
                        if (seg.isEmpty() == false) {
                            segments.add(seg);
                        }
                    }

                    if (segments.isEmpty()) {
                        // filePattern 直接位于根目录（如 "/app_%d.log"）
                        basePath = "/";
                        maxDepth = 1;
                    } else {
                        maxDepth = segments.size();

                        String first = segments.get(0);
                        if (dirPart.startsWith("/")) {
                            // Unix 绝对路径，basePath 补根（如 "/var/log" -> "/var"）
                            basePath = "/" + first;
                        } else if (first.length() == 2 && first.charAt(1) == ':') {
                            // Windows 盘符段，补斜杠避免被视为盘符相对路径（如 "C:" -> "C:/"）
                            basePath = first + "/";
                        } else {
                            basePath = first;
                        }
                    }
                }
            
                rolloverStrategy.addComponent(builder.newComponent("Delete")
                        .addAttribute("basePath", basePath)
                        .addAttribute("maxDepth", String.valueOf(maxDepth))
                        .addComponent(builder.newComponent("IfFileName")
                        // glob 只匹配归档文件（name_日期_序号.log），排除活动文件 name.log
                        .addAttribute("glob", fileName + "_*.log"))
                        .addComponent(builder.newComponent("IfAccumulatedFileSize")
                                .addAttribute("exceeds", totalSizeCap)));
            }

            fileAppender.addComponent(rolloverStrategy);
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

    /**
     * 解析大小字符串（如 "10 MB"、"1GB"），返回字节数；无法解析时返回 0
     */
    static long parseSizeBytes(String size) {
        if (size == null) {
            return 0;
        }

        String value = size.trim();

        if (value.isEmpty()) {
            return 0;
        }

        Matcher matcher = java.util.regex.Pattern
                .compile("^([\\d.]+)\\s*([a-zA-Z]+)?$")
                .matcher(value);

        if (matcher.matches() == false) {
            return 0;
        }

        try {
            double num = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2) == null ? "" : matcher.group(2).toUpperCase();

            switch (unit) {
                case "":
                case "B":
                    break;
                case "K":
                case "KB":
                    num = num * 1024;
                    break;
                case "M":
                case "MB":
                    num = num * 1024 * 1024;
                    break;
                case "G":
                case "GB":
                    num = num * 1024 * 1024 * 1024;
                    break;
                case "T":
                case "TB":
                    num = num * 1024L * 1024 * 1024 * 1024;
                    break;
                default:
                    return 0;
            }

            return (long) num;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}